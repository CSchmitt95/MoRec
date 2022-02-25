package de.carloschmitt.morec.model.recording;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.setup.Sensor;

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
            Log.d(TAG, "Leerlauf");
        } catch (Exception e) {
            Log.e(TAG, "Da ist was schief gelaufen...");
            latch.countDown();
            e.printStackTrace();
        }
        return;
    }
}
