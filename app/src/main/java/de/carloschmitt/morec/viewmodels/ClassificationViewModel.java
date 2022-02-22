package de.carloschmitt.morec.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import de.carloschmitt.morec.model.Data;

public class ClassificationViewModel extends BaseObservable {

    boolean classify_clickable(){
        return Data.state == Data.State.CONNECTED || Data.state == Data.State.CLASSIFYING;
    }
}
