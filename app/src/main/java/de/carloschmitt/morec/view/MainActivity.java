package de.carloschmitt.morec.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ActivityMainBinding;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.viewmodel.ConnectionStateViewModel;
import de.carloschmitt.morec.viewmodel.LabelPageViewModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MoRecRepository.getInstance().setContext(getApplicationContext());

        showSessionNameDialog();

        ConnectionStateViewModel viewModel = new ViewModelProvider(this).get(ConnectionStateViewModel.class);
        binding.setConnectionStateViewModel(viewModel);
        binding.setLifecycleOwner(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }

    private void showSessionNameDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint("Sitzungsname");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setTitle("Sitzungsname festlegen");
        alertDialogBuilder.setPositiveButton("festlegen",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input_text = input.getText().toString();
                if( input_text != null && !input_text.trim().isEmpty()) {
                    MoRecRepository.getInstance().setSessionName(input_text);
                }
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.create().show();
    }
}