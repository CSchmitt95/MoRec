package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ActivityRecordBinding;
import de.carloschmitt.morec.model.MovementPatternItem;
import de.carloschmitt.morec.model.MovementPatternList;

public class RecordActivity extends AppCompatActivity {
    Intent result;

    ActivityRecordBinding activityRecordBinding;

    Button btn_record;
    Button btn_save;

    MovementPatternItem mpi;
    int position;

    boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //TODO MPI muss natürlich richtig gegettet werden.
        position = getIntent().getIntExtra("Position", 0);
        mpi = getIntent().getParcelableExtra("movement");
        if(mpi == null){
            mpi = new MovementPatternItem("",0, false);
            position = MovementPatternList.ITEMS.size();
        }
        //mpi = MovementPatternList.ITEMS.get(position);
        System.out.println(position);
        activityRecordBinding = DataBindingUtil.setContentView(this, R.layout.activity_record);
        activityRecordBinding.setMp(mpi);
        isRecording = false;

        result = new Intent();
        result.putExtra("Position", position);
        setResult(0, result);

        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BackgroundRunner br = new BackgroundRunner();
                Thread backgrundThread = new Thread(br);

                if (!isRecording) {
                    isRecording = true;
                    btn_record.setText("Aufnahme Stoppen");
                    backgrundThread.start();
                } else {
                    isRecording = false;
                    btn_record.setText("Aufnahme Starten");

                    try {
                        backgrundThread.join();
                        System.out.println("Thread ended...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1, result);
                if(position == MovementPatternList.ITEMS.size()) MovementPatternList.ITEMS.add(mpi);
                else MovementPatternList.ITEMS.set(position, mpi);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
        builder.setTitle("Änderungen werden nicht gespeichert.");
        builder.setMessage("Achtung, wenn Sie über den Zurück-Button dieses Bewegungsmuster verlassen, werden die Änderungen nicht gespeichert! Wollen Sie die Änderungen Speichern?");
        builder.setPositiveButton("Ja",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(1, result);
                MovementPatternList.ITEMS.set(position, mpi);
                finish();
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class BackgroundRunner implements Runnable {

        @Override
        public void run() {
            while (isRecording) {
                mpi.addSample();
                activityRecordBinding.invalidateAll();
                System.out.println("tick");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}