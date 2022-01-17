package de.carloschmitt.morec.pages;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.SensorItemAdapter;
import de.carloschmitt.morec.dialogs.SensorDialog;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Sensor;

public class SensorPage extends Fragment implements View.OnClickListener {
    private static final String TAG = "SensorPageFragment";
    RecyclerView rv;
    Context context;

    Button btn_addSensor;
    Button btn_checkPairing;
    Button btn_connectSensors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_page_sensor, container, false);

        rv = view.findViewById(R.id.rv_sensors);
        rv.setLayoutManager(new LinearLayoutManager(context));
        Data.sensorItemAdapter = new SensorItemAdapter(Data.sensors, new SensorItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Sensor sensor) {;
                openSensorDialog(sensor);
            }
        });
        rv.setAdapter(Data.sensorItemAdapter);

        btn_addSensor = view.findViewById(R.id.btn_addSensor);
        btn_checkPairing = view.findViewById(R.id.btn_checkPairing);
        btn_connectSensors = view.findViewById(R.id.btn_checkRecording);
        if(Data.state == Data.State.CONNECTED) btn_connectSensors.setText("Sensoren Trennen");

        checkIfEmpty();

        btn_addSensor.setOnClickListener(this);
        btn_checkPairing.setOnClickListener(this);
        btn_connectSensors.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v) {
        if(v == btn_checkPairing){
            Log.d(TAG+"/PairingCheck", "Prüfe Alle pairings");
            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice btDevice;

            for(Sensor sens : Data.sensors) {
                String address = sens.getAddress();
                Log.d(TAG+"/PairingCheck", "Prüfe Sensor " + address);
                btDevice = mBtAdapter.getRemoteDevice(address);

                if (btDevice != null) {
                    if (btDevice.getBondState() == 12) {
                        Log.d(TAG+"/PairingCheck", btDevice.getAddress() + " ->Pairing vorhanden.");
                        sens.setPaired(true);
                        Data.sensorItemAdapter.notifyDataSetChanged();
                        Data.sensorItemAdapter.notifyItemChanged(Data.sensors.indexOf(sens));
                    } else {
                        Log.d(TAG+"/PairingCheck", btDevice.getAddress() + " ->Pairing Anfrage geht raus");
                        btDevice.createBond();
                    }
                }
                //Data.sensorItemAdapter.notifyDataSetChanged();
            }

        }
        if(v == btn_connectSensors){
            if(Data.state == Data.State.INACTIVE) {
                Log.d(TAG, "Verbinde Sensoren");
                btn_connectSensors.setEnabled(false);
                Data.connectSensors();
                btn_connectSensors.setText("Sensoren Trennen");
            }
            else {
                Log.d(TAG, "Stoppe Sensoren!");
                Data.disconnectSensors();
                btn_connectSensors.setText("Sensoren Verbinden");
            }
        }
        if(v == btn_addSensor){
            openSensorDialog(new Sensor("",""));
        }
    }
    public static void updateView(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Data.sensorItemAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

    private void checkIfEmpty(){
        if(Data.sensors.isEmpty()){
            btn_connectSensors.setEnabled(false);
            btn_checkPairing.setEnabled(false);
        }
    }

    private void openSensorDialog(Sensor sensor){
        Log.d(TAG, sensor.toString());
        SensorDialog sensorDialog = SensorDialog.newInstance(sensor);
        sensorDialog.setCancelable(false);
        sensorDialog.show(getChildFragmentManager(), "fragment_movementdialog");
    }
}
