package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

import java.util.List;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.SensorListRecyclerViewAdapter;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Sensor;


// Sensor 1: 00:0E:0E:1B:60:DE
// Sensor 2: 00:0E:0E:16:8F:F6

public class SensorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SensorActivity";
    RecyclerView rv;
    Context context;
    static SensorListRecyclerViewAdapter adapter;

    Button btn_addSensor;
    Button btn_checkPairing;
    Button btn_checkRecording;

    boolean running;

    private BluetoothAdapter mBtAdapter;

    List<TssMiniBluetooth> streamingSensors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


        context = getBaseContext();
        rv = findViewById(R.id.rv_sensors);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SensorListRecyclerViewAdapter(Data.sensors);
        rv.setAdapter(adapter);

        btn_addSensor = findViewById(R.id.btn_addSensor);
        btn_checkPairing = findViewById(R.id.btn_checkPairing);
        btn_checkRecording = findViewById(R.id.btn_checkRecording);

        btn_addSensor.setOnClickListener(this);
        btn_checkPairing.setOnClickListener(this);
        btn_checkRecording.setOnClickListener(this);
        running = false;
    }


    @Override
    public void onClick(View v) {
        if(v == btn_checkPairing){
            Log.d(TAG+"/PairingCheck", "Prüfe Alle pairings");
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice btDevice;

            for(Sensor dev : Data.sensors) {
                String address = dev.getAddress();
                Log.d(TAG+"/PairingCheck", "Prüfe Sensor " + address);
                btDevice = mBtAdapter.getRemoteDevice(address);

                if (btDevice != null) {
                    if (btDevice.getBondState() == 12) {
                        Log.d(TAG+"/PairingCheck", btDevice.getAddress() + " ->Pairing vorhanden.");
                        dev.setPaired(true);
                    } else {
                        Log.d(TAG+"/PairingCheck", btDevice.getAddress() + " ->Pairing Anfrage geht raus");
                        btDevice.createBond();
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }
        if(v == btn_checkRecording){
            if(!running) {
                Log.d(TAG, "Starte Sensoren");
                Data.activateSensors();

                running = true;
                btn_checkRecording.setText("Trennen");
            }
            else {
                Log.d(TAG, "Stoppe Sensoren!");
                Data.stopAllSensors();
                running = false;
                btn_checkRecording.setText("Verbinden.");
            }
        }
    }

    public static void updateList(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 100);
    }
}