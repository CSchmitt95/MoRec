package de.carloschmitt.morec.model;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

public class Sensor {
    private final String TAG = "SensorItem";

    private String name;
    private String address;
    private boolean paired; // true = gepairt, false = nicht gepairt
    private TssMiniBluetooth tssMiniBluetooth;

    public Sensor(String name, String address){
        this.name = name;
        this.address = address;
        this.paired = false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public boolean isActive() {
        if(tssMiniBluetooth != null) return tssMiniBluetooth.getIsStreaming();
        return false;
    }

    public void connect() throws TssConnectionException, TssCommunicationException {
        if(tssMiniBluetooth == null){
            tssMiniBluetooth = new TssMiniBluetooth(address, false);
        }
        if(tssMiniBluetooth.getIsConnected() == false){
            Log.d(TAG + "@" + name, "Verbinde mit Sensor... ");
            tssMiniBluetooth.connectSocket();
            Log.d(TAG + "@" + name, "Erfolg ");
            Log.d(TAG + "@" + name, "FirmwareVersion: " + tssMiniBluetooth.getFirmwareVersion());
            Log.d(TAG + "@" + name, "HardwareVersion: " + tssMiniBluetooth.getHardwareVersion());
        }
        if(tssMiniBluetooth.getIsStreaming() == false){
            tssMiniBluetooth.startStream();
            Log.d(TAG + "@" + name, "Stream gestartet");
        }
    }

    public void tare(){
        try {
            tssMiniBluetooth.setCurrentOrientationAsTare();
        } catch (TssCommunicationException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws TssConnectionException {
        tssMiniBluetooth.disconnectSocket();
    }

    public Quaternion getQuaternion() throws TssCommunicationException {
        return tssMiniBluetooth.getOrientationAsQuaternion();
    }
}


