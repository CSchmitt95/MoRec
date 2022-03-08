package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.MoRecRepository;

public class SetupPageViewModel extends AndroidViewModel {
    private static final String TAG = "SetupPageViewModel";
    private MoRecRepository moRecRepository;

    private MediatorLiveData<String> connect_button_text;
    private MutableLiveData<List<Sensor>> uiSensors;
    private MutableLiveData<Boolean> connect_button_enabled;
    private MutableLiveData<Boolean> addSensor_button_enabled;
    private MutableLiveData<Sensor> selectedSensor;


    public SetupPageViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();
        connect_button_enabled = new MutableLiveData<>();
        addSensor_button_enabled = new MutableLiveData<>();
        selectedSensor = new MutableLiveData<>();
        uiSensors = moRecRepository.getUiSensors_ui();

        connect_button_text = new MediatorLiveData<>();
        connect_button_text.addSource(moRecRepository.getState_ui(), new Observer<State>() {
            @Override
            public void onChanged(State state) {
                switch (state){
                    case INACTIVE:
                        connect_button_enabled.postValue(true);
                        connect_button_text.postValue("Verbinden");
                        addSensor_button_enabled.postValue(true);
                        break;
                    case CONNECTED:
                        connect_button_enabled.postValue(true);
                        connect_button_text.postValue("Trennen");
                        addSensor_button_enabled.postValue(false);
                        break;
                    case CONNECTING:
                        connect_button_enabled.postValue(false);
                        connect_button_text.postValue("Verbindung wird hergestellt...");
                        addSensor_button_enabled.postValue(false);
                        break;
                    case RECORDING:
                        connect_button_enabled.postValue(false);
                        connect_button_text.postValue("Aufnahme läuft noch...");
                        addSensor_button_enabled.postValue(false);
                        break;
                    case CLASSIFYING:
                        connect_button_enabled.postValue(false);
                        connect_button_text.postValue("Klassifizierung läuft noch...");
                        addSensor_button_enabled.postValue(false);
                        break;
                    case EXPORTING:
                        connect_button_enabled.postValue(false);
                        connect_button_text.postValue("Export läuft noch...");
                        addSensor_button_enabled.postValue(false);
                    case DISCONNECTING:
                        connect_button_enabled.postValue(false);
                        connect_button_text.postValue("Verbindung wird getrennt");
                        addSensor_button_enabled.postValue(false);
                }
            }
        });
    }

    public LiveData<String> getConnect_button_text() {
        return connect_button_text;
    }

    public LiveData<Boolean> getConnect_button_enabled() {
        return connect_button_enabled;
    }

    public LiveData<Boolean> getAddSensor_button_enabled() {
        return addSensor_button_enabled;
    }

    public MutableLiveData<List<Sensor>> getUiSensors() {
        return uiSensors;
    }

    public void addUISensor(Sensor sensor) { moRecRepository.addUISensor(sensor); }

    public void deleteUISensor(Sensor sensor) { moRecRepository.removeUISensor(sensor); }

    public void onConnectClicked(){
        switch (moRecRepository.getState_ui().getValue()){
            case CONNECTED:
                moRecRepository.triggerStopSignal();
                break;
            case INACTIVE:
                moRecRepository.connectSensors();
                break;
        }
    }

    public LiveData<Sensor> getSelectedSensor() {
        Log.d(TAG,"get Selected Sensor: " + selectedSensor.toString());
        return selectedSensor;
    }

    public void setSelectedSensor(Sensor newly_selected_sensor) {
        selectedSensor = new MutableLiveData<>(newly_selected_sensor);
        //if(uiSensors.getValue().contains(selectedSensor.getValue())) moRecRepository.removeUISensor(selectedSensor.getValue());
    }

    public void resetSelectedSensor(){
        String name = selectedSensor.getValue().getLive_name().getValue();
        String address = selectedSensor.getValue().getLive_address().getValue();

        Log.d(TAG, name + address);
        if(!name.trim().isEmpty() && !address.trim().isEmpty()){
            if(!uiSensors.getValue().contains(selectedSensor.getValue())) moRecRepository.addUISensor(selectedSensor.getValue());
        }
        else {
            deleteUISensor(selectedSensor.getValue());
        }
        this.selectedSensor.postValue(null);
    }
}
