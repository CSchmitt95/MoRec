package de.carloschmitt.morec.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.UIMovementAdapter;
import de.carloschmitt.morec.databinding.FragmentPageRecordBinding;
import de.carloschmitt.morec.model.recording.UIMovement;
import de.carloschmitt.morec.viewmodel.ConnectionStateViewModel;
import de.carloschmitt.morec.viewmodel.MovementListViewModel;

public class RecordPage extends Fragment {
    private static final String TAG = "MovementPageFragment";
    FragmentPageRecordBinding binding;
    ConnectionStateViewModel connectionStateViewmodel;
    MovementListViewModel movementListViewModel;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPageRecordBinding.inflate(inflater);

        connectionStateViewmodel = new ViewModelProvider(getActivity()).get(ConnectionStateViewModel.class);
        movementListViewModel = new ViewModelProvider(getActivity()).get(MovementListViewModel.class);
        binding.setConnectionStateViewModel(connectionStateViewmodel);
        binding.setMovementListViewModel(movementListViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());


        //RecylcerView
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        UIMovementAdapter uiMovementAdapter = new UIMovementAdapter(movementListViewModel.getUiMovementsLiveData().getValue(), movementListViewModel);
        binding.list.setAdapter(uiMovementAdapter);

        movementListViewModel.getUiMovementsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<UIMovement>>() {
            @Override
            public void onChanged(ArrayList<UIMovement> uiMovements) {
                uiMovementAdapter.notifyDataSetChanged();
            }
        });

        binding.btnAddMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.open_add_movment);
            }
        });


        return binding.getRoot();
    }

}
