package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.model.Data;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String SENSOR_0 = "00:0E:0E:1B:60:DE";
    String SENSOR_1 = "00:0E:0E:16:8F:F6";

    Button btn_record;
    Button btn_visualize;
    Button btn_classify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data test = Data.getInstance();

        btn_record = findViewById(R.id.btn_rec);
        btn_visualize = findViewById(R.id.btn_vis);
        btn_classify = findViewById(R.id.btn_class);

        btn_record.setOnClickListener(this);
        btn_visualize.setOnClickListener(this);
        btn_classify.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == btn_record){
            Intent record = new Intent(this, ListActivity.class);
            startActivity(record);
        }
        else if(v == btn_visualize){
            Intent sensors = new Intent(this, SensorActivity.class);
            startActivity(sensors);
        }
        else if(v == btn_classify){

        }
        else{
            Log.d("MainActivity-ActionListener","The FUCK?!?");
        }
    }
}