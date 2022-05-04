package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.util.State;

public class ConnectionRunner implements Runnable{
    private static final String TAG = "SensorConnectionRunner";

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        MoRecRepository moRecRepository = MoRecRepository.getInstance();
        moRecRepository.resetStopSignal();
        CountDownLatch done = moRecRepository.getSignalStop();
        try {
            for (Sensor sensor : moRecRepository.getUiSensors_ui().getValue()) {
                //Sensor Pairen...
                //TODO: Check for Bluetooth pairing.
                sensor.setPaired();

                //Sensor Verbinden...
                //ActiveSensor new_Sensor = new ActiveSensor(sensor.getLive_name().getValue(), sensor.getLive_address().getValue());
                //moRecRepository.getActiveSensors().add(new_Sensor);
                sensor.createConnection();
                sensor.setConnected();
            }
            moRecRepository.setState(State.CONNECTED);
            Log.d(TAG, "Erflogreich alle Sensoren verbunden!");
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new BackgroundRunner(done),0,1000/ Constants.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);
            Log.d(TAG,"Warte auf Beendigung...");
            done.await();
            moRecRepository.setState(State.DISCONNECTING);
            Log.d(TAG,"Trennungssignal erhalten...");
            future.cancel(false);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Verbindungsaufbau.");
            Log.e(TAG, e.getMessage());
        }
        for(Sensor sensor : moRecRepository.getUiSensors()){
            try{
                sensor.destroyConnection();
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
            sensor.setDisconnected();
        }
        moRecRepository.setState(State.INACTIVE);
        //moRecRepository.disconnectSensors();
        long end = System.currentTimeMillis();
        List<Long> log = moRecRepository.getRuntime_log().get("ConnectionRunner");
        if(log == null) log = new ArrayList<>();
        log.add(end-start);
        moRecRepository.getRuntime_log().put("ConnectionRunner",log);
        Log.d(TAG,"Beendet");
    }
}
