package de.carloschmitt.morec.viewmodel;

import android.text.BoringLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.State;
import de.carloschmitt.morec.view.MainActivity;

public class ConnectionStateViewModel extends ViewModel {

    private MutableLiveData<String> statusText;
    private State applicationState = State.INACTIVE;

    public MutableLiveData<String> getStatusText(){
        if(statusText == null ) return new MutableLiveData<String>(applicationState.name());
        else return statusText;
    }

    private MutableLiveData<String> connect_button_text;
    public MutableLiveData<String> getConnect_button_text(){
        if(connect_button_text == null) {
            connect_button_text = new MutableLiveData<String>();
            if(applicationState == State.INACTIVE) connect_button_text.setValue("Verbinden!");
            if(applicationState == State.CONNECTING) connect_button_text.setValue("Verbindung wird hergestellt...");
            if(applicationState == State.CONNECTED) connect_button_text.setValue("Verbindung trennen");
        }
        return connect_button_text;
    }

    private MutableLiveData<Boolean> connect_button_enabled;
    public MutableLiveData<Boolean> getConnect_button_enabled(){
        if(connect_button_enabled == null) {
            connect_button_enabled = new MutableLiveData<Boolean>();
            if(applicationState == State.INACTIVE) connect_button_enabled.setValue(true);
            if(applicationState == State.CONNECTING) connect_button_enabled.setValue(false);
            if(applicationState == State.CONNECTED) connect_button_enabled.setValue(true);
        }
        return connect_button_enabled;
    }

    private MutableLiveData<String> record_button_text;
    public MutableLiveData<String> getRecord_button_text(){
        if(record_button_text == null) {
            record_button_text = new MutableLiveData<String>();
            if(applicationState == State.CONNECTED) record_button_text.setValue("Aufnahme starten");
            else if(applicationState == State.RECORDING) record_button_text.setValue("Aufnahme stoppen");
            else record_button_text.setValue("Zum Aufnehmen erst verbinden...");
        }
        return record_button_text;
    }

    private MutableLiveData<Boolean> record_button_enabled;
    public MutableLiveData<Boolean> getRecord_button_enabled(){
        if(record_button_enabled == null) {
            record_button_enabled = new MutableLiveData<Boolean>();
            if(applicationState == State.CONNECTED) record_button_enabled.setValue(true);
            else if(applicationState == State.RECORDING) record_button_enabled.setValue(true);
            else record_button_enabled.setValue(false);
        }
        return record_button_enabled;
    }
}
