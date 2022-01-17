package de.carloschmitt.morec.recording;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;

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

    private class RecorderConnector implements Runnable{

        @Override
        public void run() {
            for (Sensor sensor : Data.sensors) {
                String name = sensor.getName();
                try {
                    sensor.connect();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = Data.sensors.indexOf(sensor);
                            Data.sensorItemAdapter.notifyItemChanged(pos);
                        }
                    }, 100);
                } catch (TssCommunicationException | TssConnectionException e) {
                    Log.d(TAG + "@" + name, "Fehler beim Verbindungsaufbau!");
                    e.printStackTrace();
                    break;
                }
            }
            Data.state = Data.State.CONNECTED;
        }
    }
}
