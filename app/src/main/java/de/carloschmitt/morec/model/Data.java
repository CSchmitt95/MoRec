package de.carloschmitt.morec.model;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.carloschmitt.morec.activities.SensorActivity;

public class Data {
    public static final String TAG = "Data";
    public static final int WINDOW_SIZE_IN_S = 3;
    public static final double OVERLAP_IN_S = 0.25;
    public static final int SAMPLES_PER_SECOND = 200;

    private static final int MAX_SAMPLES = WINDOW_SIZE_IN_S * SAMPLES_PER_SECOND;
    private static final int OVERLAP = (int) OVERLAP_IN_S * SAMPLES_PER_SECOND;
    private static Data instance;

    public static List<MovementPattern> movementPatterns;
    public static List<Sensor> sensors;
    public static SampleBuffer sampleBuffer;

    public static boolean activeConnection;         // Bestimmt Lebenszeit des Sampler Threads
    public static MovementPattern selectedMovement;

    private Data(){
        movementPatterns = new ArrayList<>();
        sensors = new ArrayList<>();
        sampleBuffer = new SampleBuffer(MAX_SAMPLES, OVERLAP);
        selectedMovement = null;

        sensors.add(new Sensor("Links", "00:0E:0E:16:8F:F6")); // UUID: 00001101-0000-1000-8000-00805f9b34fb
        sensors.add(new Sensor("Rechts","00:0E:0E:1B:60:DE")); // UUID: 00001101-0000-1000-8000-00805f9b34fb

        movementPatterns.add(new MovementPattern("Gehen",false));
    }

    public static Data getInstance(){
        if (instance == null) return new Data();
        else return instance;
    }

    public static boolean isRecording(){
        return sampleBuffer.getCurrentMovementPattern() != null;
    }

    public static void startRecording(){
        sampleBuffer.setCurrentMovementPattern(selectedMovement);
    }

    public static void stopRecording(){
        sampleBuffer.setCurrentMovementPattern(null);
    }

    public static void activateSensors(){
        Log.d(TAG, "aktiviere alle Sensoren...");
        activeConnection = true;
        Thread connectorThread = new Thread(new SensorConnector());
        connectorThread.start();
    }

    public static void stopAllSensors(){
        Log.d(TAG, "Stoppe alle Sensoren...");
        activeConnection = false;
    }

    public static class SensorConnector implements Runnable{
        private final String TAG = "SensorConnector";
        @Override
        public void run() {
            boolean success = true;
            // Alle Sensoren streamen lassen.
            for (Sensor sensor : sensors) {
                String name = sensor.getName();
                try {
                    if (sensor.tssMiniBluetooth == null)
                        sensor.tssMiniBluetooth = new TssMiniBluetooth(sensor.getAddress(), false);

                    TssMiniBluetooth tssMiniBluetooth = sensor.tssMiniBluetooth;
                    Log.d(TAG + "@" + name, "Verbinde mit Sensor... ");
                    tssMiniBluetooth.connectSocket();
                    tssMiniBluetooth.startStream();

                    Log.d(TAG + "@" + name, "Verbindung hergestellt!");
                    SensorActivity.updateList();

                } catch (TssCommunicationException | TssConnectionException e) {
                    success = false;
                    Log.d(TAG + "@" + name, "Fehler beim Verbindungsaufbau!");
                    e.printStackTrace();
                    break;
                }
            }


            //Aufnehmen der Daten.
            if(success){
                while (activeConnection) {
                    try {
                        Sample sample = new Sample();
                        for (Sensor sensor : sensors) {
                            if(!sensor.isActive()) break;
                            Quaternion sample_data = sensor.tssMiniBluetooth.getOrientationAsQuaternion();
                            String sensor_name = sensor.getName();
                            sample.addSample(sensor_name, sample_data);
                        }
                        sampleBuffer.add(sample);
                        if(sampleBuffer.isSaturated()) {
                            sampleBuffer.getCurrentMovementPattern().addPattern(sampleBuffer.getSamples());
                        }
                        else Log.d(TAG, sampleBuffer.status());
                        Thread.sleep(5 );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            for(Sensor sensor : sensors){
                if(sensor.isActive()) {
                    try {
                        sensor.tssMiniBluetooth.disconnectSocket();
                        SensorActivity.updateList();
                    } catch (TssConnectionException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "Stoppe Thread.");
        }
    }

    public static void exportData(){
        /*for(MovementPattern movements : movementPatterns){
            try
            {
                File root = new File(Environment.getExternalStorageDirectory(), "Aufnahmen");
                if (!root.exists()) {
                    root.mkdirs();
                }

                for(List<Sample> patternList : movements.patternList){
                    for(String sensor : patternList.get(0).samples.keySet()){
                        File gpxfile = new File(root, movements.name + "_" + sensor + ".csv");
                        FileWriter writer = new FileWriter(gpxfile);
                        for(Sample sample : patternList){
                            Quaternion q = sample.samples.get(sensor);
                            writer.append(q.toString());
                        }
                        writer.flush();
                        writer.close();
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }*/
    }
}
