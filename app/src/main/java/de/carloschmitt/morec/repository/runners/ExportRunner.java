package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.ExportUtil;
import de.carloschmitt.morec.repository.util.State;

public class ExportRunner implements Runnable{

    @Override
    public void run() {
        MoRecRepository moRecRepository = MoRecRepository.getInstance();
        moRecRepository.setState(State.EXPORTING);
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
                Log.d("EXPORT", gpxfile.getAbsolutePath());
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
        moRecRepository.setState(State.INACTIVE);
    }
}
