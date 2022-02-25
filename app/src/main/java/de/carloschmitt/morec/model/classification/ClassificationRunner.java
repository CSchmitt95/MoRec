package de.carloschmitt.morec.model.classification;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.setup.Sensor;

public class ClassificationRunner implements Runnable{
    private static final String TAG = "ClassificationRunner";
    ClassificationBuffer buffer;
    CountDownLatch latch;

    public ClassificationRunner(ClassificationBuffer buffer){
        this.buffer = buffer;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            HashMap<Sensor, Quaternion> samples = new HashMap<>();
            for (Sensor sensor : ApplicationController.sensors) {

            }
            if(!buffer.addSample(samples)){
                latch.countDown();
            }
        } catch (Exception e) {
            Log.e(TAG, "Da ist was schief gelaufen...");
            latch.countDown();
            e.printStackTrace();
        }
        return;
    }
}
