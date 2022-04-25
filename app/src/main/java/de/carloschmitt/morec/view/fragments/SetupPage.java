package de.carloschmitt.morec.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.carloschmitt.morec.adapters.UISensorAdapter;
import de.carloschmitt.morec.databinding.PageSetupBinding;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;

public class SetupPage extends Fragment {
    private static final String TAG = "SetupPage";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PageSetupBinding binding = PageSetupBinding.inflate(inflater);

        //ViewModel und observation.
        SetupPageViewModel setupPageViewModel = new ViewModelProvider(getActivity()).get(SetupPageViewModel.class);
        binding.setSetupPageViewModel(setupPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //RecylcerView
        binding.rvSensors.setLayoutManager(new LinearLayoutManager(getActivity()));
        UISensorAdapter uiSensorAdapter = new UISensorAdapter(setupPageViewModel);
        setupPageViewModel.getUiSensors().observe(getViewLifecycleOwner(), list -> uiSensorAdapter.submitList(new ArrayList<>(list)));
        binding.rvSensors.setAdapter(uiSensorAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(setupPageViewModel.getAddSensor_button_enabled().getValue()){
                    setupPageViewModel.deleteUISensor(uiSensorAdapter.getUISensorAt(viewHolder.getBindingAdapterPosition()));
                    //Toast.makeText(getActivity(), "Sensor entfernt", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Sensor kann gerade nicht entfernt werden", Toast.LENGTH_SHORT).show();
                    //uiSensorAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                }
            }
        }).attachToRecyclerView(binding.rvSensors);

        return binding.getRoot();
    }

}
