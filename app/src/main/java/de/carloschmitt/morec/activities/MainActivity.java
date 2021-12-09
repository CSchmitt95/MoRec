package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.carloschmitt.morec.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_record;
    Button btn_visualize;
    Button btn_classify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        }
        else if(v == btn_classify){

        }
        else{
            Log.d("MainActivity-ActionListener","The FUCK?!?");
        }
    }
}