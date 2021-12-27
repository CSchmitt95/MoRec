package de.carloschmitt.morec.model;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

import de.carloschmitt.morec.activities.SensorActivity;

public class Sensor {
    private final String TAG = "SensorItem";

    String name;
    String address;
    boolean paired; // true = gepairt, false = nicht gepairt
    TssMiniBluetooth tssMiniBluetooth;

    public Sensor(String name, String address){
        this.name = name;
        this.address = address;
        this.paired = false;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public boolean isActive() {
        if(tssMiniBluetooth != null) return tssMiniBluetooth.getIsStreaming() && tssMiniBluetooth.getIsConnected();
        return false;
    }
}


