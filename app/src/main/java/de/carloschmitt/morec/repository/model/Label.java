package de.carloschmitt.morec.repository.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Label {
    private static int label_counter = 0;
    private final LiveData<Integer> label_id;
    private String label_text;
    private MutableLiveData<String> label_text_ui;
    private MutableLiveData<Boolean> holdToRecord;
    private LiveData<String> recording_stats;

    public Label(String name){
        label_id = new MutableLiveData<>(label_counter++);
        label_text = name;
        label_text_ui = new MutableLiveData<>(label_text);
        holdToRecord = new MutableLiveData<>(false);
        recording_stats = new MutableLiveData<>("None");
    }

    public MutableLiveData<String> getLabel_text_ui() {
        return label_text_ui;
    }

    public void setLabel_text_ui(String text){
        label_text = text;
        label_text_ui.postValue(label_text);
    }

    public LiveData<Integer> getLabel_id(){
        return label_id;
    }

    public MutableLiveData<Boolean> getHoldToRecord() {
        return holdToRecord;
    }

    public void setHoldToRecord(Boolean holdToRecord) {
        this.holdToRecord.postValue(holdToRecord);
    }

    public LiveData<String> getRecording_stats() {
        return recording_stats;
    }

    public String getLabel_text() {
        return label_text;
    }
}
