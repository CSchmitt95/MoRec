package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ActivityRecordBinding;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.MovementPattern;

public class RecordActivity extends AppCompatActivity {
    Intent result;

    ActivityRecordBinding activityRecordBinding;

    Button btn_record;
    Button btn_delete;

    boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRecordBinding = DataBindingUtil.setContentView(this, R.layout.activity_record);
        activityRecordBinding.setMp(Data.selectedMovement);
        isRecording = false;

        result = new Intent();
        setResult(0, result);

        btn_record = findViewById(R.id.btn_record);
        btn_record.setEnabled(Data.activeConnection);
        btn_record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Data.isRecording()){
                    Data.stopRecording();
                    btn_record.setText("Aufname Starten");
                    activityRecordBinding.invalidateAll();
                }
                else{
                    Data.startRecording();
                    btn_record.setText("Aufnahme Stoppen");
                }
                activityRecordBinding.notifyChange();
            }
        });

        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.movementPatterns.remove(Data.selectedMovement);
                finish();
            }
        });
    }
}