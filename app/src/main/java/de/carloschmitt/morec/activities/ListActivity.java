package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.MovementPatternRecyclerViewAdapter;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.MovementPattern;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "List Activity";

    public RecyclerView rv;
    static MovementPatternRecyclerViewAdapter mprva;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        context = getBaseContext();
        rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(context));
        mprva = new MovementPatternRecyclerViewAdapter(Data.movementPatterns);
        rv.setAdapter(mprva);

        Button btn_addMovement = findViewById(R.id.btn_AddMovement);
        btn_addMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, RecordActivity.class);

                MovementPattern mp = new MovementPattern("",false);
                Data.selectedMovement = mp;
                Data.movementPatterns.add(mp);

                startActivityForResult(intent,2);
            }
        });

        Button btn_exportData = findViewById(R.id.btn_Export);
        btn_exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.exportData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Pattern Liste: " + Data.movementPatterns.size());
        super.onActivityResult(requestCode, resultCode, data);
        Data.stopRecording();
        Data.selectedMovement = null;
        mprva.notifyDataSetChanged();
    }
}