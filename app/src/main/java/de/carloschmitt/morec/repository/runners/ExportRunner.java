package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.ConfusionMatrix;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sample;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.util.ExportUtil;
import de.carloschmitt.morec.repository.util.State;

public class ExportRunner implements Runnable{

    private static final String TAG = "ExportRunner";

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
            HashMap<Label, FileWriter> writers = new HashMap<>();
            for(Label label : moRecRepository.getLabels().getValue()){
                File gpxfile = new File(records, label.getLabel_text_ui().getValue() + ".csv");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("MovementName,SensorName,Record_id,x0,y0 z0,w0... wn, xn, yn, zn\n");
                writers.put(label,writer);
            }
            List<Sample> firstRecordBuffer = moRecRepository.getSensors().getValue().get(0).getRecordBuffer();
            int full_length = firstRecordBuffer.size();
            int record_id = 0;
            int index = ExportUtil.findStartOfNextLabel(0, firstRecordBuffer);

            while (index < full_length) {
                moRecRepository.setExportProgress("Exportiere ( " + (index*100)/full_length + "% )");
                int length = ExportUtil.getLengthOfCurrentLabel(index, firstRecordBuffer);
                int extract_start;
                int extract_end;

                if (length >= Constants.MAX_SAMPLES) {
                    extract_start = index;
                    extract_end = index + length;
                } else {
                    int diff = Constants.MAX_SAMPLES - length;
                    extract_start = index - diff / 2;
                    extract_end = index + length + diff / 2;
                }
                Label label = ExportUtil.findLabelWithId(firstRecordBuffer.get(index).getLabel_id());
                if(label != null){
                    for(Sensor sensor : moRecRepository.getSensors().getValue()){
                        //writers.get(label).append(label.getLabel_text() + "," + sensor.getName() + ","+ record_id + ExportUtil.getQuaternionStringFromTo(extract_start, extract_end, sensor.getRecordBuffer()) +"\n" );
                        ExportUtil.writeQuaternionsToFile(label.getLabel_text_ui().getValue(), sensor.getLive_name().getValue(),record_id, extract_start, extract_end, sensor.getRecordBuffer(),writers.get(label));
                        writers.get(label).flush();
                    }
                    record_id++;
                }
                else{
                    Log.e(TAG, "Coulding find Label for id" + firstRecordBuffer.get(index).getLabel_id());
                }
                index = ExportUtil.findStartOfNextLabel(index+length, firstRecordBuffer);
            }
            for(FileWriter writer : writers.values()){
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
                writer.append("," + String.join(",", moRecRepository.getModelLabels())+ ",DontKnow\n");
                int line = 0;
                for(String string : moRecRepository.getModelLabels()){
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
