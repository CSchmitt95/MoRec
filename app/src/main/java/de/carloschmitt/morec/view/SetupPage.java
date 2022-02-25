package de.carloschmitt.morec.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import de.carloschmitt.morec.adapters.UISensorAdapter;
import de.carloschmitt.morec.databinding.FragmentPageSetupBinding;
import de.carloschmitt.morec.model.setup.Sensor;
import de.carloschmitt.morec.model.setup.UISensor;
import de.carloschmitt.morec.view.dialogues.SetupDialogue;
import de.carloschmitt.morec.viewmodel.ConnectionStateViewModel;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;

public class SetupPage extends Fragment {
    private static final String TAG = "SensorPageFragment";
    FragmentPageSetupBinding binding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        binding = FragmentPageSetupBinding.inflate(inflater);

        //ViewModels
        SetupPageViewModel setupPageViewModel = new ViewModelProvider(getActivity()).get(SetupPageViewModel.class);
        ConnectionStateViewModel connectionStateViewmodel = new ViewModelProvider(getActivity()).get(ConnectionStateViewModel.class);
        binding.setConnectionStateViewModel(connectionStateViewmodel);
        binding.setSetupPageViewModel(setupPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //RecylcerView
        binding.rvSensors.setLayoutManager(new LinearLayoutManager(context));
        UISensorAdapter uiSensorAdapter = new UISensorAdapter();
        binding.rvSensors.setAdapter(uiSensorAdapter);

        setupPageViewModel.getUiSensorList().observe(getViewLifecycleOwner(), new Observer<ArrayList<UISensor>>() {
            @Override
            public void onChanged(ArrayList<UISensor> uiSensors) {
                uiSensorAdapter.updateList(uiSensors);
            }
        });

        return binding.getRoot();
    }


    private void openSensorDialog(Sensor sensor){
        Log.d(TAG, sensor.toString());
        SetupDialogue setupDialogue = SetupDialogue.newInstance(sensor);
        setupDialogue.setCancelable(false);
        setupDialogue.show(getChildFragmentManager(), "fragment_movementdialog");
    }
}
