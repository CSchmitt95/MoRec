package de.carloschmitt.morec.model.setup;

public class UISensor {
    String name;
    String address;
    boolean holdToRecord;
    boolean paired;
    boolean connected;

    public UISensor(){
        name = "Sensor Name";
        address = "00:00:00:00:00:00";
        holdToRecord = false;
        paired = false;
        connected = false;
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

    public boolean isHoldToRecord() {
        return holdToRecord;
    }

    public void setHoldToRecord(boolean holdToRecord) {
        this.holdToRecord = holdToRecord;
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
