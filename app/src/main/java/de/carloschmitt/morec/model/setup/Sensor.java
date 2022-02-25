package de.carloschmitt.morec.model.setup;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;

public class Sensor {
    private final String TAG = "Sensor";
    private String name;
    private String address;
    private boolean paired; // true = gepairt, false = nicht gepairt
    private boolean connected;

    public Sensor(String name, String address) throws TssConnectionException {
        this.name = name;
        this.address = address;
        this.paired = false;
        this.connected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}


