package de.carloschmitt.morec.model.recording;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;

import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.State;
import de.carloschmitt.morec.model.setup.Sensor;

public class Recorder {
    private final String TAG = "Recorder";

    private RecordingScheduler recordingScheduler;

    private Movement movement;

    public Recorder(){
        recordingScheduler = null;

        Thread connectorThread = new Thread(new RecorderConnector());
        connectorThread.start();
    }

    public boolean startRecording(Movement movement){
        if(ApplicationController.state == State.CONNECTED){
            this.movement = movement;
            //for(Sensor s : ApplicationController.sensors) s.setCurrentOrientationAsTare();
            recordingScheduler = new RecordingScheduler(movement);
            Thread recorderThread = new Thread(recordingScheduler);
            recorderThread.start();
            ApplicationController.state = State.RECORDING;
            return true;
        }
        return false;
    }

    public void stopRecording(){
        if(ApplicationController.state == State.RECORDING){
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
                Toast.makeText(ApplicationController.applicationContext,  message , Toast.LENGTH_SHORT).show();
            }
        }, 100);
    }

    private class RecorderConnector implements Runnable{

        @Override
        public void run() {
            List<Sensor> connected = new LinkedList<>();
            for (Sensor sensor : ApplicationController.sensors) {
                String name = sensor.getName();
                try {
                    //sensor.connect();
                    connected.add(sensor);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = ApplicationController.sensors.indexOf(sensor);
                            ApplicationController.sensorItemAdapter.notifyItemChanged(pos);
                            Toast.makeText(ApplicationController.applicationContext,  sensor.getName() + " verbunden", Toast.LENGTH_SHORT).show();
                        }
                    }, 100);
                } catch (Exception e) {
                    toastToMain("Fehler beim Verbinden mit " + sensor.getName());
                    Log.d(TAG + "@" + name, "Fehler beim Verbindungsaufbau!");
                    for(Sensor s : connected){
                        try{
                            //s.disconnectSocket();
                        } catch (Exception fe){
                            Log.d(TAG, "Fataler Fehler deluxe: " + e.getLocalizedMessage());
                        }
                    }
                    ApplicationController.state = State.INACTIVE;
                    e.printStackTrace();
                    return;
                }
            }
            toastToMain("Alle Sensoren verbunden");
            ApplicationController.state = State.CONNECTED;
        }
    }
}
