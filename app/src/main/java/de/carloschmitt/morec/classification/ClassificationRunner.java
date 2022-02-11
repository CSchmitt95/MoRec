package de.carloschmitt.morec.classification;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Sensor;

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
            for (Sensor sensor : Data.sensors) {
                if(sensor.isActive()){
                    Quaternion q = sensor.getQuaternion();
                    samples.put(sensor, q);
                } else{
                    throw new Exception();
                }
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
