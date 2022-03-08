package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.util.State;

public class ClassificationPageViewModel extends AndroidViewModel {
    private MoRecRepository moRecRepository;
    private MediatorLiveData<String> classify_button_text;
    private MutableLiveData<Boolean> classify_button_enabled;
    private MutableLiveData<String> result_text;

    public ClassificationPageViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();

        classify_button_enabled = new MutableLiveData<>();
        result_text = moRecRepository.getClassificationResult();

        classify_button_text = new MediatorLiveData<>();
        classify_button_text.addSource(moRecRepository.getState_ui(), new Observer<State>() {
            @Override
            public void onChanged(State state) {
                switch (state){
                    case INACTIVE:
                        classify_button_enabled.postValue(false);
                        classify_button_text.postValue("Erst Verbinden...");
                        break;
                    case CONNECTED:
                        classify_button_enabled.postValue(true);
                        classify_button_text.postValue("Klassifizieren");
                        break;
                    case CONNECTING:
                        classify_button_enabled.postValue(false);
                        classify_button_text.postValue("Verbindung wird noch hergestellt...");
                        break;
                    case RECORDING:
                        classify_button_enabled.postValue(false);
                        classify_button_text.postValue("Aufnahme läuft noch...");
                        break;
                    case CLASSIFYING:
                        classify_button_enabled.postValue(true);
                        classify_button_text.postValue("Klassifizierung stoppen");
                        break;
                    case EXPORTING:
                        classify_button_enabled.postValue(false);
                        classify_button_text.postValue("Export läuft noch...");
                }
            }
        });
    }

    public MediatorLiveData<String> getClassify_button_text() {
        return classify_button_text;
    }

    public MutableLiveData<Boolean> getClassify_button_enabled() {
        return classify_button_enabled;
    }

    public MutableLiveData<String> getResult_text(){
        return result_text;
    }

    public void OnClick(View view){
        switch (moRecRepository.getState()){
            case CONNECTED:
                moRecRepository.startClassifying();
                break;
            case CLASSIFYING:
                moRecRepository.stopClassifying();
                break;
        }
    }
}
