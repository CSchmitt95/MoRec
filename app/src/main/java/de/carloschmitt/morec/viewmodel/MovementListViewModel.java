package de.carloschmitt.morec.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import de.carloschmitt.morec.model.recording.Movement;
import de.carloschmitt.morec.model.recording.UIMovement;

public class MovementListViewModel extends AndroidViewModel {
    private static final String TAG = "RecordPageViewModel";
    ArrayList<UIMovement> uiMovements;
    private MutableLiveData<ArrayList<UIMovement>> uiMovementsLiveData;
    private UIMovement currentDetailViewMovement;

    public MovementListViewModel(@NonNull Application application) {
        super(application);
        uiMovements = new ArrayList<UIMovement>();
        uiMovementsLiveData = new MutableLiveData<ArrayList<UIMovement>>(uiMovements);
        currentDetailViewMovement = null;
    }

    public MutableLiveData<ArrayList<UIMovement>> getUiMovementsLiveData() {
        return uiMovementsLiveData;
    }

    public void addMovement(UIMovement uiMovement){
        uiMovements.add(uiMovement);
        uiMovementsLiveData.setValue(uiMovements);
    }

    public void removeCurrentDetailViewMovement(){
        uiMovements.remove(currentDetailViewMovement);
        uiMovementsLiveData.setValue(uiMovements);
        currentDetailViewMovement = null;
    }

    public void setDetailViewMovement(int pos){
        currentDetailViewMovement = uiMovements.get(pos);
    }

    public UIMovement getDetailViewMovement(){
        return currentDetailViewMovement;
    }

    public void resetDetailViewMovement(){
        currentDetailViewMovement = null;
        uiMovementsLiveData.setValue(uiMovements);
    }
}
