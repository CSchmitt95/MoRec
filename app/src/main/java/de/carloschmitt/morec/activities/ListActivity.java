package de.carloschmitt.morec.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.MovementPatternRecyclerViewAdapter;
import de.carloschmitt.morec.model.MovementPatternList;

public class ListActivity extends AppCompatActivity {

    public static RecyclerView rv;
    public static MovementPatternList movementPatternList;
    MovementPatternRecyclerViewAdapter mprva;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        movementPatternList = new MovementPatternList();
        movementPatternList.generateExampleData();

        context = getBaseContext();
        rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(context));
        mprva = new MovementPatternRecyclerViewAdapter(MovementPatternList.ITEMS);
        rv.setAdapter(mprva);

        Button btn_addMovement = findViewById(R.id.btn_AddMovement);
        btn_addMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, RecordActivity.class);
                startActivityForResult(intent,2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Activity kam mit result zurück!");
        if (requestCode == 1 && resultCode == Activity.RESULT_FIRST_USER) {
            System.out.println("MovementPatternItem updated!");
            int result_pos = data.getIntExtra("Position",0);
            mprva.notifyItemChanged(result_pos);
            Toast.makeText(context, "Änderungen erfolgreich gespeichert",Toast.LENGTH_SHORT).show();
        }
        if(requestCode == 2 && resultCode == Activity.RESULT_FIRST_USER){
            System.out.println("MovementPatternItem added!");
            int result_pos = data.getIntExtra("Position",0);
            mprva.notifyItemChanged(result_pos);
            Toast.makeText(context, "Änderungen erfolgreich gespeichert",Toast.LENGTH_SHORT).show();

        }
        else Toast.makeText(context, "Nichts gespeichert",Toast.LENGTH_LONG).show();

    }
}