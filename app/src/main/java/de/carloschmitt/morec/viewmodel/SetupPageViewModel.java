package de.carloschmitt.morec.viewmodel;

import android.nfc.Tag;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.State;
import de.carloschmitt.morec.model.setup.UISensor;

public class SetupPageViewModel extends ViewModel {
    private static final String TAG = "SetupPageViewModel";
    private MutableLiveData<ArrayList<UISensor>> uiSensorList;

    public MutableLiveData<ArrayList<UISensor>> getUiSensorList() {
        if(uiSensorList == null) {
            uiSensorList = new MutableLiveData<>(new ArrayList<UISensor>());
        }
        return uiSensorList;
    }

    public void addUISensor(){

    }

}
