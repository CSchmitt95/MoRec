package de.carloschmitt.morec.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.carloschmitt.morec.adapters.MovementItemAdapter;
import de.carloschmitt.morec.adapters.SensorItemAdapter;
import de.carloschmitt.morec.recording.Recorder;
import de.carloschmitt.morec.recording.Recording;

public class Data {
    //Wichtige Konstanten
    private static Data instance;
    public static final String TAG = "Data";
    public static final int WINDOW_SIZE_IN_S = 3;
    public static final double OVERLAP_IN_S = 0.25;
    public static final int SAMPLES_PER_SECOND = 125;
    public static final int SLEEP_TIME = 1000/SAMPLES_PER_SECOND;
    public static final int OVERLAP = (int) Math.ceil(OVERLAP_IN_S * SAMPLES_PER_SECOND);
    public static final int MAX_SAMPLES = WINDOW_SIZE_IN_S * SAMPLES_PER_SECOND + 2 * OVERLAP ;

    public static Context applicationContext;

    public enum State{
        INACTIVE,
        CONNECTING,
        CONNECTED,
        RECORDING,
        CLASSIFYING,
        EXPORTING
    }

    public static State state;
    // Listen bezüglich aller Sensoren und Bewegungen
    public static List<Movement> movements;
    public static List<Sensor> sensors;
    public static Recorder recorder;

    //Wichtige Zustände:
    public static MovementItemAdapter movementItemAdapter;
    public static SensorItemAdapter sensorItemAdapter;

    //Testing

    private Data(){
        movements = new ArrayList<>();
        sensors = new ArrayList<>();
        recorder = null;


        sensors.add(new Sensor("Gürtel", "00:0E:0E:16:8F:F6")); // UUID: 00001101-0000-1000-8000-00805f9b34fb
        //sensors.add(new Sensor("Handgelenk","00:0E:0E:1B:60:DE")); // UUID: 00001101-0000-1000-8000-00805f9b34fb

        movements.add(new Movement("Gehen",false));
        movements.add(new Movement("Stehen",false));
        movements.add(new Movement("Stolpern",true));

        movementItemAdapter = null;
        sensorItemAdapter = null;
        state = State.INACTIVE;
    }

    public static Data getInstance(){
        if (instance == null) return new Data();
        else return instance;
    }

    public static void startRecording(Movement movement){
        if(state == State.CONNECTED){
            recorder.startRecording(movement);
        }
    }

    public static void stopRecording(){
        if(state == State.RECORDING){
            recorder.stopRecording();
        }
    }

    public static void connectSensors(){
        if(state == State.INACTIVE) {
            Log.d(TAG, "Verbinde alle Sensoren...");
            recorder = new Recorder();
            state = State.CONNECTING;
        }
    }

    public static void disconnectSensors(){
        if(state == State.CONNECTED){
            Log.d(TAG, "Trenne alle Sensoren...");
            if(recorder != null) {
                recorder.destroy();
                recorder = null;
            }
        }
    }

    public static void exportData(ProgressBar bar){
        Thread exporterThread = new Thread(new DataExporter(bar));
        exporterThread.start();
    }

    private static class DataExporter implements Runnable{
        String TAG = "DateExporter";
        ProgressBar bar;

        public DataExporter(ProgressBar bar){
            this.bar = bar;
        }

        @Override
        public void run() {
            try
            {
                String foldername = new SimpleDateFormat("yyyyMMdd_HH:mm").format(new Date());
                File root = new File(applicationContext.getExternalFilesDir(null).toString(), foldername);
                if (!root.exists()) {
                    root.mkdirs();
                }
                bar.setMax(movements.size()*100);
                bar.setIndeterminate(false);
                for(Movement movement : Data.movements){
                    bar.setProgress(movements.indexOf(movement)*100);
                    File gpxfile = new File(root, movement.name + ".csv");
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append("MovementName,SensorName,Record_id,");
                    writer.append("x0,y0 z0,w0... wn, xn, yn, zn\n");
                    for(Recording recording : movement.getRecordings()){
                        bar.setProgress(movements.indexOf(movement) * 100+ ( movement.getRecordings().indexOf(recording) / movement.getRecordings().size())*99 );
                        writer.append(recording.getMovement().getName() + "," + recording.getSensor().getName() + "," + recording.getSession_id() + recording.getQuaternionsAsString() + "\n");
                    }
                    writer.flush();
                    writer.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    bar.setVisibility(View.GONE);
                }
            }, 100);
        }
    }
}
