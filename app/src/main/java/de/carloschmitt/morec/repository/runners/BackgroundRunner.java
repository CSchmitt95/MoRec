package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.ClassificationUtil;
import de.carloschmitt.morec.repository.Constants;
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
            case EXPORTING:
                Log.d(TAG, "Something went terribly wrong: Background runner scheduled when EXPORTING");
                break;
        }
        long end = System.currentTimeMillis();
        if(end - start > 1000/Constants.SAMPLES_PER_SECOND) Log.e(TAG,"Background Runner läuft zu langsam (" + (end-start) + "/" + 1000/Constants.SAMPLES_PER_SECOND + "ms )");
        List<Long> log = moRecRepository.getRuntime_log().get("BackgroundRunner");
        if(log == null) log = new ArrayList<>();
        log.add(end-start);
        moRecRepository.getRuntime_log().put("BackgroundRunner",log);
    }

    private void recordQuaternion(){
        boolean a_sensor_died = false;
        for(Sensor sensor : moRecRepository.getUiSensors()){
            try{
                sensor.recordQuaternion();
                if(sensor.died()) a_sensor_died = true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(a_sensor_died) done.countDown();
    }

    private void doClassification(){
        //Checken ob ein Buffer bereit ist.
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
        List<List<Quaternion>> input = new ArrayList<>();
        for(Sensor s : moRecRepository.getUiSensors()){
            List<Quaternion> rawQuaternions = s.getLastNQuaternions(Constants.SAMPLES_PER_SECOND*Constants.WINDOW_SIZE_IN_S +1);
            input.add(rawQuaternions);
        }
        long afterData = System.currentTimeMillis();
        Log.d(TAG, "Daten gesammelt. ( " + (afterData - beforeData) + "ms )");

        Log.d(TAG, "Starte Classification Thread (input size: " + input.size() + ")");
        ClassificationRunner classificationRunner = new ClassificationRunner(input);
        Thread classificationThread = new Thread(classificationRunner);
        classificationThread.start();
        Log.d(TAG, "Classification Thread gestartet");
    }


}
