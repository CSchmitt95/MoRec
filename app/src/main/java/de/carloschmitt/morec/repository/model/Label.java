package de.carloschmitt.morec.repository.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.Constants;

/**
 * Datenklasse zum Halten der Daten für die Label-Karte.
 * Label Counter regelt die Label_IDs
 * Die Klasse stellt Daten für die UI bereit
 */
public class Label {
    private static int label_counter = 0;
    private final LiveData<Integer> label_id;

    private MutableLiveData<String> label_text_ui;
    private MutableLiveData<Boolean> holdToRecord;
    private MediatorLiveData<String> recording_stats;

    public Label(String name){
        label_id = new MutableLiveData<>(label_counter++);
        label_text_ui = new MutableLiveData<>(name);
        holdToRecord = new MutableLiveData<>(false);
        MutableLiveData<Integer> number_of_samples = MoRecRepository.getInstance().getSensors().getValue().get(0).getNumberOfSamplesForUI(label_id.getValue());
        recording_stats = new MediatorLiveData<>();
        recording_stats.addSource(number_of_samples, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int hundreds = (integer*100/Constants.SAMPLES_PER_SECOND) %100;
                int seconds = (integer/ Constants.SAMPLES_PER_SECOND) % 60;
                int minutes = (integer/ (Constants.SAMPLES_PER_SECOND*60)) % 60;
                int hours = integer / (Constants.SAMPLES_PER_SECOND*60*60);
                recording_stats.postValue(String.format("%01d:%02d:%02d,%02d", hours, minutes, seconds, hundreds));
            }
        });
    }

    public MutableLiveData<String> getLabel_text_ui() {
        return label_text_ui;
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

    public MediatorLiveData<String> getRecording_stats() {
        return recording_stats;
    }
}
