package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.ClassificationUtil;
import de.carloschmitt.morec.repository.util.Constants;
import de.carloschmitt.morec.repository.util.ExportUtil;
import de.carloschmitt.morec.repository.util.State;


public class BackgroundRunner implements Runnable{
    MoRecRepository moRecRepository;
    private static final String TAG = "BackgroundRunner";
    private final CountDownLatch done;

    public BackgroundRunner(CountDownLatch done){
        this.done = done;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        moRecRepository = MoRecRepository.getInstance();

        switch (moRecRepository.getState()){
            case EXPORTING:
                exportData();
                break;
            case CLASSIFYING:
                recordQuaternion();
                doClassification();
                break;
            case RECORDING:
                recordQuaternion();
                break;
            case CONNECTED:
                recordQuaternion();
                break;
            case INACTIVE:
                done.countDown();
                break;
            case CONNECTING:
                Log.d(TAG, "Something went terribly wrong: Background runner scheduled when CONNECTING");
                break;
        }
        long end = System.currentTimeMillis();
        if(end - start > 1000/Constants.SAMPLES_PER_SECOND) Log.e(TAG,"Background Runner läuft zu langsam (" + (end-start) + "/" + 1000/Constants.SAMPLES_PER_SECOND + "ms )");
    }

    private void recordQuaternion(){
        for(Sensor sensor : moRecRepository.getUiSensors()){
            try{
                sensor.recordQuaternion();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doClassification(){
        //Checken ob ein Buffer eventuell noch nicht bereit ist.
        for(Sensor s : moRecRepository.getUiSensors()){
            if (!s.bufferIsSaturated()){
                Log.d(TAG, "Buffer ist noch nicht bereit");
                return;
            }
        }
        //Checke ob der Cooldown schon abgelaufen ist...
        if(!moRecRepository.checkClassificationCooldown()) return;
        moRecRepository.resetClassificationCooldown();

        // Sensordaten müssen in diesem Thread abgegriffen werden um Safe zu bleiben...
        Log.d(TAG, "Sammle Sensordaten...");
        long beforeData = System.currentTimeMillis();
        List<Quaternion> input = new ArrayList<>();
        for(Sensor s : moRecRepository.getUiSensors()){
            Log.d(TAG, s.getName());
            Log.d(TAG, "Get last n...");
            List<Quaternion> rawQuaternions = s.getLastNQuaternions(Constants.SAMPLES_PER_SECOND*Constants.WINDOW_SIZE_IN_S+1);
            Log.d(TAG, "Get diff... (" + rawQuaternions.size() +" )");
            List<Quaternion> diffQuaternions = ClassificationUtil.rawQuaternionsToDiffQuaternions(rawQuaternions);
            Log.d(TAG, "Get null...(" + diffQuaternions.size() +  ")");
            Log.d(TAG, "Nullification first Quaternion: " + diffQuaternions.get(0).toString());
            List<Quaternion> nullQuaternions = ClassificationUtil.nullifyQuaternions(diffQuaternions);
            Log.d(TAG, "Genullte ...(" + nullQuaternions.size() +  ")");

            input.addAll(nullQuaternions);
        }
        long afterData = System.currentTimeMillis();
        Log.d(TAG, "Daten gesammelt. ( " + (afterData - beforeData) + "ms )");

        Log.d(TAG, "Starte Classification Thread");
        ClassificationRunner classificationRunner = new ClassificationRunner(input);
        Thread classificationThread = new Thread(classificationRunner);
        classificationThread.start();
        Log.d(TAG, "Classification Thread gestartet");
    }

    public void exportData(){
        try
        {
            String foldername = new SimpleDateFormat("yyyyMMdd_HH:mm").format(new Date());
            File root = new File(moRecRepository.getContext().getExternalFilesDir(null).toString(), foldername);
            if (!root.exists()) {
                root.mkdirs();
            }
            //bar.setMax(movements.size()*100);
            //bar.setIndeterminate(false);
            for(Label label : moRecRepository.getUiLabels().getValue()){
                //bar.setProgress(movements.indexOf(movement)*100);
                File gpxfile = new File(root, label.getLabel_text() + ".csv");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("MovementName,SensorName,Record_id,x0,y0 z0,w0... wn, xn, yn, zn\n");
                int record_id = 0;
                int index = ExportUtil.findNextStartOf(label, 0, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                while (index >= 0){
                    int end = index + ExportUtil.getLengthOfCurrentLabel(index, moRecRepository.getUiSensors().get(0).getRecordBuffer());

                    for(Sensor sensor : moRecRepository.getUiSensors()){
                        writer.append(label.getLabel_text() + "," + sensor.getName() + ","+ record_id + ExportUtil.getQuaternionStringFromTo(index, end, sensor.getRecordBuffer()) +"\n" );
                    }
                    record_id++;
                    index = ExportUtil.findNextStartOf(label, end + 1, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                }
                writer.flush();
                writer.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        moRecRepository.setState(State.CONNECTED);
    }
}
