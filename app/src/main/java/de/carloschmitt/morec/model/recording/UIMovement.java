package de.carloschmitt.morec.model.recording;

public class UIMovement {
    private static int label_counter = 0;
    final int label_number;
    String name;
    Boolean holdToRecord;
    int sec_counter;

    public UIMovement(){
        label_number = label_counter++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHoldToRecord() {
        return holdToRecord;
    }

    public void setHoldToRecord(Boolean holdToRecord) {
        this.holdToRecord = holdToRecord;
    }

    public int getSec_counter() {
        return sec_counter;
    }

    public void setSec_counter(int sec_counter) {
        this.sec_counter = sec_counter;
    }
}
