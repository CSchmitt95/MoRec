package de.carloschmitt.morec.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ActivityMainBinding;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.viewmodel.ConnectionStateViewModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MoRecRepository.getInstance().setContext(getApplicationContext());

        ConnectionStateViewModel viewModel = new ViewModelProvider(this).get(ConnectionStateViewModel.class);
        binding.setConnectionStateViewModel(viewModel);
        binding.setLifecycleOwner(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }
}