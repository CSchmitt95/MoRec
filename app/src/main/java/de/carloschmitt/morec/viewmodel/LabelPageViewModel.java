package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;

public class LabelPageViewModel extends AndroidViewModel {
    private static final String TAG = "RecordPageViewModel";
    MoRecRepository moRecRepository;
    private MutableLiveData<List<Label>> labelList;
    private Label currentDetailViewMovement;


    public LabelPageViewModel(@NonNull Application application) {
        super(application);
        moRecRepository = MoRecRepository.getInstance();
        currentDetailViewMovement = null;
        labelList = moRecRepository.getUiLabels();
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
}
