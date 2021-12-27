package de.carloschmitt.morec.model;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.Map;

public class Sample {
    Map<String, Quaternion> samples;

    public Sample(){
        samples = new HashMap<>();
    }

    public void addSample(String sensor, Quaternion data){
        samples.put(sensor, data);
    }
}
