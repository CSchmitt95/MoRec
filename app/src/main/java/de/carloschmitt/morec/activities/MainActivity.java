package de.carloschmitt.morec.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.pages.ClassificationPage;
import de.carloschmitt.morec.pages.MovementPage;
import de.carloschmitt.morec.pages.SensorPage;
import de.carloschmitt.morec.model.Data;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data.getInstance();
        Data.applicationContext = getApplicationContext();
        Data.testShit();

        SensorPage sensorPage = new SensorPage();
        MovementPage movementPage = new MovementPage();
        ClassificationPage classificationPage = new ClassificationPage();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sensorPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, sensorPage).commit();
                        Log.d(TAG, "sensorPage Clicked");
                        return true;

                    case R.id.movementPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, movementPage).commit();
                        Log.d(TAG, "movementPage Clicked");
                        return true;
                    case R.id.classifcationPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, classificationPage).commit();
                        Log.d(TAG, "classificationPage Clicked");
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.sensorPage);
    }
}