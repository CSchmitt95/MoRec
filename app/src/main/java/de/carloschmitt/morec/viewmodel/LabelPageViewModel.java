package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.util.State;

public class LabelPageViewModel extends AndroidViewModel {
    private static final String TAG = "RecordPageViewModel";
    MoRecRepository moRecRepository;
    private MutableLiveData<List<Label>> labelList;
    private MediatorLiveData<String> exportButtonText;
    private MutableLiveData<Boolean> exportButtonEnabled;
    private Label currentDetailViewMovement;


    public LabelPageViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();
        currentDetailViewMovement = null;
        labelList = moRecRepository.getLabels();
        exportButtonText = new MediatorLiveData<>();
        exportButtonEnabled = new MutableLiveData<>();
        exportButtonText.addSource(moRecRepository.getState(), new Observer<State>() {
            @Override
            public void onChanged(State state) {
                switch (state){
                    case INACTIVE:
                        exportButtonEnabled.postValue(true);
                        exportButtonText.postValue("Daten Exportieren");
                        break;
                    case CONNECTED:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue("Erst Sensoren trennen");
                        break;
                    case CONNECTING:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue("Verbindung wird hergestellt...");
                        break;
                    case RECORDING:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue("Aufnahme läuft noch...");
                        break;
                    case CLASSIFYING:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue("Klassifizierung läuft noch...");
                        break;
                    case EXPORTING:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue(moRecRepository.getExportProgress().getValue());
                        break;
                    case DISCONNECTING:
                        exportButtonEnabled.postValue(false);
                        exportButtonText.postValue("Verbindung wird noch getrennt");
                        break;
                }
            }
        });
        exportButtonText.addSource(moRecRepository.getExportProgress(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(moRecRepository.getState().getValue() == State.EXPORTING) exportButtonText.postValue(moRecRepository.getExportProgress().getValue());
            }
        });
    }

    public MutableLiveData<List<Label>> getLabelList() {
        return labelList;
    }

    public void addLabel(Label label){
        moRecRepository.addLabel(label);
    }

    public void onExportClicked(){
        moRecRepository.exportData();
    }

    public void onClick(View view){

    }

    public void removeLabel(Label label){
        moRecRepository.removeLabel(label);
    }

    public void setDetailViewMovement(Label label){
        currentDetailViewMovement = label;
    }

    public Label getDetailViewMovement(){
        return currentDetailViewMovement;
    }

    public void resetDetailViewMovement(){
        currentDetailViewMovement = null;
    }

    public MediatorLiveData<String> getExportButtonText() {
        return exportButtonText;
    }

    public MutableLiveData<Boolean> getExportButtonEnabled() {
        return exportButtonEnabled;
    }
}
