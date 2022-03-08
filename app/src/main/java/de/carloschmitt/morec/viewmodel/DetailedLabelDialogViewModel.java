package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.Constants;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;

public class DetailedLabelDialogViewModel extends AndroidViewModel {
    private final String TAG = "DetailedLabelDialogViewModel";
    private MoRecRepository moRecRepository;

    private MutableLiveData<Label> uiLabel;

    private MediatorLiveData<String> record_button_text;
    private MutableLiveData<Boolean> record_button_enabled;

    private MediatorLiveData<String> record_stats;

    public DetailedLabelDialogViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();
        record_button_enabled = new MutableLiveData<>();
        record_button_text = new MediatorLiveData<>();
        record_button_text.addSource(moRecRepository.getState_ui(), new Observer<State>() {
            @Override
            public void onChanged(State state) {
                switch (state){
                    case INACTIVE:
                        record_button_enabled.postValue(false);
                        record_button_text.postValue("Aufnehmen");
                        break;
                    case CONNECTED:
                        record_button_enabled.postValue(true);
                        record_button_text.postValue("Aufnehmen");
                        break;
                    case CONNECTING:
                        record_button_enabled.postValue(false);
                        record_button_text.postValue("Verbindung wird noch hergestellt...");
                        break;
                    case RECORDING:
                        record_button_enabled.postValue(true);
                        record_button_text.postValue("Aufnahme beenden");
                        break;
                    case CLASSIFYING:
                        record_button_enabled.postValue(false);
                        record_button_text.postValue("Klassifizierung läuft noch...");
                        break;
                    case EXPORTING:
                        record_button_enabled.postValue(false);
                        record_button_text.postValue("Export läuft noch...");
                }
            }
        });
        record_stats = new MediatorLiveData<>();
    }

    public MediatorLiveData<String> getRecord_button_text() {
        return record_button_text;
    }

    public MutableLiveData<Boolean> getRecord_button_enabled() {
        return record_button_enabled;
    }

    public MutableLiveData<Label> getUiLabel() {
        if(uiLabel == null) uiLabel = new MutableLiveData<>(new Label(""));
        return uiLabel;
    }

    public void setUiLabel(Label label) {
        this.uiLabel = new MutableLiveData<>(label);
        if(moRecRepository.getUiSensors().size() != 0) {
            Sensor sensor = moRecRepository.getUiSensors().get(0);
            int label_id = uiLabel.getValue().getLabel_id().getValue();
            MutableLiveData<Integer> number_of_samples = sensor.getNumberOfSamplesForUI(label_id);
            record_stats.addSource(number_of_samples, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    record_stats.postValue(integer/Constants.SAMPLES_PER_SECOND + " Sekunden");
                }
            });
        }
        else{
            record_stats.postValue("Keine Daten...");
        }
    }

    public String getMovementName(){
        return getUiLabel().getValue().getLabel_text_ui().getValue();
    }

    public void setMovementName(String name){
        Label new_Label = uiLabel.getValue();
        new_Label.getLabel_text_ui().postValue(name);
        uiLabel.postValue(new_Label);
    }

    public boolean getHoldToRecordChecked(){
        return uiLabel.getValue().getHoldToRecord().getValue();
    }

    public void setHoldToRecordChecked(boolean checked){
        Label new_Label = uiLabel.getValue();
        new_Label.setHoldToRecord(checked);
        uiLabel.postValue(new_Label);
    }

    public LiveData<String> getRecord_stats(){
        return record_stats;
    }

    public boolean OnTouchListener(View view, MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(moRecRepository.getState() == State.CONNECTED) moRecRepository.startRecording(uiLabel.getValue().getLabel_id().getValue());
            else moRecRepository.stopRecording();
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(uiLabel.getValue().getHoldToRecord().getValue()) moRecRepository.stopRecording();
        }
        return false;
    }
}
