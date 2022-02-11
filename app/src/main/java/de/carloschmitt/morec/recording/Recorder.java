package de.carloschmitt.morec.recording;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;

import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;
import de.carloschmitt.morec.model.Sensor;
import de.carloschmitt.morec.pages.SensorPage;

public class Recorder {
    private final String TAG = "Recorder";

    private RecordingScheduler recordingScheduler;

    private Movement movement;

    public Recorder(){
        recordingScheduler = null;

        Thread connectorThread = new Thread(new RecorderConnector());
        connectorThread.start();
    }

    public void destroy(){
        for (Sensor sensor : Data.sensors) {
            try {
                sensor.disconnect();
            } catch (TssConnectionException e) {
                e.printStackTrace();
            }
        }
        SensorPage.updateView();
    }

    public boolean startRecording(Movement movement){
        if(Data.state == Data.State.CONNECTED){
            this.movement = movement;
            for(Sensor s : Data.sensors) s.tare();
            recordingScheduler = new RecordingScheduler(movement);
            Thread recorderThread = new Thread(recordingScheduler);
            recorderThread.start();
            Data.state = Data.State.RECORDING;
            return true;
        }
        return false;
    }

    public void stopRecording(){
        if(Data.state == Data.State.RECORDING){
            recordingScheduler.latch.countDown();
            movement.finishCurrentRecordings();
            recordingScheduler = null;
        }
    }

    private void toastToMain(String message){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Data.applicationContext,  message , Toast.LENGTH_SHORT).show();
            }
        }, 100);
    }

    private class RecorderConnector implements Runnable{

        @Override
        public void run() {
            List<Sensor> connected = new LinkedList<>();
            for (Sensor sensor : Data.sensors) {
                String name = sensor.getName();
                try {
                    sensor.connect();
                    connected.add(sensor);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = Data.sensors.indexOf(sensor);
                            Data.sensorItemAdapter.notifyItemChanged(pos);
                            Toast.makeText(Data.applicationContext,  sensor.getName() + " verbunden", Toast.LENGTH_SHORT).show();
                        }
                    }, 100);
                } catch (TssCommunicationException | TssConnectionException e) {
                    toastToMain("Fehler beim Verbinden mit " + sensor.getName());
                    Log.d(TAG + "@" + name, "Fehler beim Verbindungsaufbau!");
                    for(Sensor s : connected){
                        try{
                            s.disconnect();
                        } catch (Exception fe){
                            Log.d(TAG, "Fataler Fehler deluxe: " + e.getLocalizedMessage());
                        }
                    }
                    Data.state = Data.State.INACTIVE;
                    e.printStackTrace();
                    return;
                }
            }
            toastToMain("Alle Sensoren verbunden");
            Data.state = Data.State.CONNECTED;
        }
    }
}
