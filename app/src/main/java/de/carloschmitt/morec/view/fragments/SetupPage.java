package de.carloschmitt.morec.view.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import de.carloschmitt.morec.databinding.PageSetupBinding;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;
import de.carloschmitt.morec.viewmodel.adapters.SensorAdapter;

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
        SensorAdapter sensorAdapter = new SensorAdapter(setupPageViewModel);
        setupPageViewModel.getUiSensors().observe(getViewLifecycleOwner(), list -> sensorAdapter.submitList(new ArrayList<>(list)));
        binding.rvSensors.setAdapter(sensorAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(setupPageViewModel.getAddSensor_button_enabled().getValue()){

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    setupPageViewModel.deleteUISensor(sensorAdapter.getUISensorAt(viewHolder.getBindingAdapterPosition()));
                                    Toast.makeText(getActivity(), "Sensor entfernt", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    sensorAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Soll der Sensor wirklich entfernt werden?").setPositiveButton("Ja", dialogClickListener)
                            .setNegativeButton("Nein", dialogClickListener).show();
                }
                else{
                    Toast.makeText(getActivity(), "Sensor kann gerade nicht entfernt werden", Toast.LENGTH_SHORT).show();
                    sensorAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                }
            }
        }).attachToRecyclerView(binding.rvSensors);

        return binding.getRoot();
    }

}
