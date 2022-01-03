package de.carloschmitt.morec.model;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

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

    public static boolean activeConnection;         // Bestimmt Lebenszeit des Sampler Threads
    public static MovementPattern selectedMovement;

    public static TapeRecorder tapeRecorder;


    private Data(){
        movementPatterns = new ArrayList<>();
        sensors = new ArrayList<>();
        selectedMovement = null;

        sensors.add(new Sensor("Links", "00:0E:0E:16:8F:F6")); // UUID: 00001101-0000-1000-8000-00805f9b34fb
        sensors.add(new Sensor("Rechts","00:0E:0E:1B:60:DE")); // UUID: 00001101-0000-1000-8000-00805f9b34fb

        movementPatterns.add(new MovementPattern("Gehen",false));
        tapeRecorder = new TapeRecorder();
    }

    public static Data getInstance(){
        if (instance == null) return new Data();
        else return instance;
    }

    public static boolean isRecording(){
        return tapeRecorder.isRecording();
    }

    public static void startRecording(){
        for(Sensor sensor : sensors){
            tapeRecorder.insertTape(new Tape(sensor, selectedMovement));
        }
        tapeRecorder.pressRecord();
    }

    public static void stopRecording(){
        tapeRecorder.pressStop();
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
                    long before = System.currentTimeMillis();
                    long after = 0;
                    try {
                        for (Sensor sensor : sensors) {
                            if(!sensor.isActive()) break;
                            Quaternion sample_data = sensor.tssMiniBluetooth.getOrientationAsQuaternion();
                            tapeRecorder.recordQuaternion(sample_data, sensor);
                            after = System.currentTimeMillis();

                        }
                        Thread.sleep(5 );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(after == 0 && (after - before) > 5) Log.d(TAG,"Loop-Ausführungszeit zu lang: " + (after-before));
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

    }
}
