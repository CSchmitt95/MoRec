package de.carloschmitt.morec.repository.runners;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

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

public class ConnectionRunner implements Runnable {
    private static final String TAG = "SensorConnectionRunner";
    CountDownLatch done;

    public ConnectionRunner(CountDownLatch disconnectSignal){
        this.done = disconnectSignal;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        MoRecRepository moRecRepository = MoRecRepository.getInstance();
        List<Long> runtime_log = moRecRepository.getRuntime_log().get("ConnectionRunner");
        if (runtime_log == null) runtime_log = new ArrayList<>();
        MoRecRepository.getInstance().setState(State.CONNECTING);
        try {
            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice btDevice;
            for (Sensor sensor : moRecRepository.getSensors().getValue()) {

                //Pairing Prüfen...
                btDevice = mBtAdapter.getRemoteDevice(sensor.getLive_address().getValue());
                if (btDevice != null && ActivityCompat.checkSelfPermission(moRecRepository.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (btDevice.getBondState() == 12) {
                        Log.d(TAG + "/PairingCheck", btDevice.getAddress() + " ->Pairing vorhanden.");
                        sensor.setPaired();
                    } else {
                        Log.d(TAG + "/PairingCheck", btDevice.getAddress() + " ->Pairing Anfrage geht raus");
                        btDevice.createBond();
                    }
                }

                //Sensor Verbinden...
                sensor.createConnection();
                sensor.setConnected();
            }
            moRecRepository.setState(State.CONNECTED);
            Log.d(TAG, "Erflogreich alle Sensoren verbunden!");
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            long connected = System.currentTimeMillis();
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new BackgroundRunner(done),0,1000/ Constants.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);
            Log.d(TAG,"Warte auf Beendigung...");
            runtime_log.add(connected-start);
            done.await();
            moRecRepository.setState(State.DISCONNECTING);
            Log.d(TAG,"Trennungssignal erhalten...");
            future.cancel(false);
            long start_disconnect = System.currentTimeMillis();
            runtime_log.add(start_disconnect-connected);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Verbindungsaufbau.");
            Log.e(TAG, e.getMessage());
        }
        for(Sensor sensor : moRecRepository.getSensors().getValue()){
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
        runtime_log.add(end-start);
        moRecRepository.getRuntime_log().put("ConnectionRunner",runtime_log);
        Log.d(TAG,"Beendet");
    }
}
