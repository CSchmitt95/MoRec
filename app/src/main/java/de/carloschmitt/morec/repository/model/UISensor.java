package de.carloschmitt.morec.repository.model;

import android.util.Log;
import android.view.View;

import androidx.databinding.InverseMethod;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import java.util.logging.StreamHandler;

import de.carloschmitt.morec.R;

public class UISensor {
    private static final String TAG = "UISensor";
    private MutableLiveData<String> name;
    private MutableLiveData<String> address;
    private MutableLiveData<Boolean> paired;
    private MutableLiveData<Boolean> connected;
    private MutableLiveData<String> sensor_health;

    public UISensor(String name, String address){
        this.name = new MutableLiveData<>(name);
        this.address = new MutableLiveData<>(address);
        paired = new MutableLiveData<>(false);
        connected = new MutableLiveData<>(false);
    }

    public LiveData<Boolean> getPaired() {
        return paired;
    }

    public LiveData<Boolean> getConnected() {
        return connected;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name.postValue(name.getValue());
    }

    public MutableLiveData<String> getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address.postValue(address);
    }

    public void setPaired() {
        this.paired.postValue(true);
    }

    public void setConnected() {
        this.connected.postValue(true);
    }

    public void setDisconnected(){ this.connected.postValue(false); }

    public MutableLiveData<String> getSensor_health() {
        return sensor_health;
    }
}
