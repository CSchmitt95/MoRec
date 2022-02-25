package de.carloschmitt.morec.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.carloschmitt.morec.model.recording.UIMovement;

public class DetailedMovementDialogViewModel extends ViewModel {
    MutableLiveData<UIMovement> uiMovement;

    public MutableLiveData<UIMovement> getUiMovement() {
        if(uiMovement == null) uiMovement = new MutableLiveData<>(new UIMovement());
        return uiMovement;
    }

    public void setUiMovement(UIMovement uiMovement) {
        this.uiMovement = new MutableLiveData<UIMovement>(uiMovement);
    }

    public String getMovementName(){
        return getUiMovement().getValue().getName();
    }

    public void setMovementName(String name){
        UIMovement new_uiMovement = uiMovement.getValue();
        new_uiMovement.setName(name);
        uiMovement.postValue(new_uiMovement);
    }
}
