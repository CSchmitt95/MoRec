package de.carloschmitt.morec.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.MoRecRepository;

public class ConnectionStateViewModel extends AndroidViewModel {
    private static final String TAG = "ConnectionStateViewModel";
    private MutableLiveData<State> applicationState = MoRecRepository.getInstance().getState();

    public MediatorLiveData<String> status;

    public ConnectionStateViewModel(@NonNull Application application) {
        super(application);
    }
}
