package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.ConfusionMatrix;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.util.ExportUtil;
import de.carloschmitt.morec.repository.util.State;

public class ExportRunner implements Runnable{

    @Override
    public void run() {
        MoRecRepository moRecRepository = MoRecRepository.getInstance();
        moRecRepository.setState(State.EXPORTING);
        try
        {
            //Export Recorded Data
            long before = System.currentTimeMillis();
            //String foldername = new SimpleDateFormat("yyyyMMdd_HH:mm").format(new Date());
            String foldername = moRecRepository.getSessionName();
            File root = new File(moRecRepository.getContext().getExternalFilesDir(null).toString(), foldername);
            File records = new File(moRecRepository.getContext().getExternalFilesDir(null).toString(), foldername+"/records");
            File eval = new File(moRecRepository.getContext().getExternalFilesDir(null).toString(), foldername+"/eval");
            File runtimes = new File(moRecRepository.getContext().getExternalFilesDir(null).toString(), foldername+"/runtimes");
            if (!root.exists()) {
                root.mkdirs();
                records.mkdirs();
                eval.mkdirs();
                runtimes.mkdirs();
            }
            //bar.setMax(movements.size()*100);
            //bar.setIndeterminate(false);
            for(Label label : moRecRepository.getUiLabels().getValue()){
                //bar.setProgress(movements.indexOf(movement)*100);
                File gpxfile = new File(records, label.getLabel_text() + ".csv");
                Log.d("EXPORT", gpxfile.getAbsolutePath());
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("MovementName,SensorName,Record_id,x0,y0 z0,w0... wn, xn, yn, zn\n");
                int record_id = 0;
                int index = ExportUtil.findNextStartOf(label, 0, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                while (index >= 0){
                    int length = ExportUtil.getLengthOfCurrentLabel(index, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                    //int end = index + ExportUtil.getLengthOfCurrentLabel(index, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                    int extract_start;
                    int extract_end;

                    if(length >= Constants.MAX_SAMPLES){
                        extract_start = index;
                        extract_end = index + length;
                    }
                    else{
                        int diff = Constants.MAX_SAMPLES - length;
                        extract_start = index - diff/2;
                        extract_end = index + length + diff/2;
                    }

                    for(Sensor sensor : moRecRepository.getUiSensors()){
                        writer.append(label.getLabel_text() + "," + sensor.getName() + ","+ record_id + ExportUtil.getQuaternionStringFromTo(extract_start, extract_end, sensor.getRecordBuffer()) +"\n" );
                    }
                    record_id++;
                    index = ExportUtil.findNextStartOf(label, index+length + 1, moRecRepository.getUiSensors().get(0).getRecordBuffer());
                }
                writer.flush();
                writer.close();
            }

            //Export Evaluation Matrices
            List<ConfusionMatrix> matrices = new LinkedList<>();
            matrices.add(moRecRepository.getCm_Grtl());
            matrices.add(moRecRepository.getCm_Handgelenk());
            matrices.add(moRecRepository.getCm_GrtlHandgelenk());

            for(ConfusionMatrix cm : matrices){
                File gpxfile = new File(eval, cm.getName() + ".csv");
                Log.d("EXPORT", gpxfile.getAbsolutePath());
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("," + String.join(",", moRecRepository.getLabels())+ ",DontKnow\n");
                int line = 0;
                for(String string : moRecRepository.getLabels()){
                    writer.append(string + cm.getLine(line)+"\n");
                    line++;
                }
                writer.flush();
                writer.close();
            }

            //ExportRuntime Stats
            for(String key : moRecRepository.getRuntime_log().keySet()){
                moRecRepository.getRuntime_log().get(key);
                File gpxfile = new File(runtimes, key + ".csv");
                Log.d("EXPORT", gpxfile.getAbsolutePath());
                FileWriter writer = new FileWriter(gpxfile);

                List<Long> log = moRecRepository.getRuntime_log().get(key);
                for(Long entry: log){
                    writer.write(entry + ",");
                }

                writer.flush();
                writer.close();
            }

            //Export Export Stats
            long after = System.currentTimeMillis();
            File gpxfile = new File(root, "ExportStats.csv");
            Log.d("EXPORT", gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            writer.write("Export Time: " + Long.toString((after-before)) + "ms");
            writer.flush();
            writer.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        moRecRepository.setState(State.INACTIVE);
    }
}
