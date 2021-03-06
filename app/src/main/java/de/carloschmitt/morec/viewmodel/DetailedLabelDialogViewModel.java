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
import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;

public class DetailedLabelDialogViewModel extends AndroidViewModel {
    private final String TAG = "DetailedLabelDialogViewModel";
    private MoRecRepository moRecRepository;

    private MutableLiveData<Label> uiLabel;

    private MediatorLiveData<String> record_button_text;
    private MutableLiveData<Boolean> record_button_enabled;

    public DetailedLabelDialogViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();
        record_button_enabled = new MutableLiveData<>();
        record_button_text = new MediatorLiveData<>();
        record_button_text.addSource(moRecRepository.getState(), new Observer<State>() {
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
                        record_button_text.postValue("Klassifizierung l??uft noch...");
                        break;
                    case EXPORTING:
                        record_button_enabled.postValue(false);
                        record_button_text.postValue("Export l??uft noch...");
                }
            }
        });
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
    }

    public boolean OnTouchListener(View view, MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(moRecRepository.getState().getValue() == State.CONNECTED) moRecRepository.startRecording(uiLabel.getValue().getLabel_id().getValue());
            else moRecRepository.stopRecording();
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(uiLabel.getValue().getHoldToRecord().getValue()) moRecRepository.stopRecording();
        }
        return false;
    }
}
