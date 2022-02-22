package de.carloschmitt.morec.recording;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.dialogs.MovementDialog;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;
import de.carloschmitt.morec.model.Sensor;

/**
 * Ein Daten aufnahmezyklus. Wird in entsprechender Frequenz ausgelöst um die Daten aufzunehmen.
 */
public class RecordingRunner implements Runnable {
    private static final String TAG = "Recording Runner";
    private CountDownLatch latch;
    private Movement movement;

    public RecordingRunner(CountDownLatch latch, Movement movement){
        this.latch = latch;
        this.movement = movement;
    }
    @Override
    public void run() {
        try {
            Map<Sensor, Quaternion> samples = new HashMap<>();
            for (Sensor sensor : Data.sensors) {
                if(sensor.isActive()){
                    Quaternion q = sensor.getQuaternion();
                    Log.d(TAG, q.toString());
                    samples.put(sensor, q);
                } else{
                    throw new Exception();
                }
            }
            if(movement.addSamples(samples)){
                latch.countDown();
                movement.finishCurrentRecordings();
            }
        } catch (Exception e) {
            Log.e(TAG, "Da ist was schief gelaufen...");
            latch.countDown();
            e.printStackTrace();
        }
        return;
    }
}
