package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.repository.util.Constants;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.ActiveSensor;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.model.UISensor;

public class ConnectionRunner implements Runnable{
    private static final String TAG = "SensorConnectionRunner";

    @Override
    public void run() {
        MoRecRepository moRecRepository = MoRecRepository.getInstance();
        moRecRepository.resetStopSignal();
        CountDownLatch done = moRecRepository.getSignalStop();
        try {
            for (UISensor sensor : moRecRepository.getUiSensors_ui().getValue()) {
                //Sensor Pairen...
                //TODO: Check for Bluetooth pairing.
                sensor.setPaired();

                //Sensor Verbinden...
                ActiveSensor new_Sensor = new ActiveSensor(sensor.getName().getValue(), sensor.getAddress().getValue());
                moRecRepository.getActiveSensors().add(new_Sensor);
                sensor.setConnected();
            }
            moRecRepository.setState(State.CONNECTED);
            Log.d(TAG, "Erflogreich alle Sensoren verbunden!");
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new BackgroundRunner(done),0,1000/ Constants.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);
            Log.d(TAG,"Warte auf Beendigung...");
            done.await();
            moRecRepository.setState(State.CONNECTING);
            Log.d(TAG,"Trennungssignal erhalten...");
            future.cancel(false);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Verbindungsaufbau.");
            Log.e(TAG, e.getMessage());
        }
        moRecRepository.disconnectSensors();
        Log.d(TAG,"Beendet");
    }
}
