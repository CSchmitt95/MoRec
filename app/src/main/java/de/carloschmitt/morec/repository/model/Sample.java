package de.carloschmitt.morec.repository.model;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

public class Sample {
    private final Quaternion quaternion;
    private final int label_id;

    public Sample(Quaternion q, int id){
        quaternion = q;
        label_id = id;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    public int getLabel_id() {
        return label_id;
    }
}
