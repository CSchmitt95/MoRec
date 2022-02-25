package de.carloschmitt.morec.view.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import de.carloschmitt.morec.databinding.FragmentDialogMovementBinding;
import de.carloschmitt.morec.viewmodel.ConnectionStateViewModel;
import de.carloschmitt.morec.viewmodel.DetailedMovementDialogViewModel;
import de.carloschmitt.morec.viewmodel.MovementListViewModel;

public class DetailedMovementDialogue extends DialogFragment {
    private final String TAG = "MovementDetailDialog";
    MovementListViewModel movementListViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentDialogMovementBinding binding = FragmentDialogMovementBinding.inflate(LayoutInflater.from(getContext()),null, false);

        ConnectionStateViewModel connectionStateViewModel = new ViewModelProvider(getActivity()).get(ConnectionStateViewModel.class);
        movementListViewModel = new ViewModelProvider(getActivity()).get(MovementListViewModel.class);

        DetailedMovementDialogViewModel detailedMovementDialogViewModel = new ViewModelProvider(this).get(DetailedMovementDialogViewModel.class);
        detailedMovementDialogViewModel.setUiMovement(movementListViewModel.getDetailViewMovement());

        binding.setConnectionStateViewModel(connectionStateViewModel);
        binding.setDetailedMovementDialogViewModel(detailedMovementDialogViewModel);
        binding.setLifecycleOwner(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        builder.setTitle("Aufzeichnung");
        builder.setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                movementListViewModel.removeCurrentDetailViewMovement();
            }
        });

        builder.setPositiveButton("Schliessen",  null);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        movementListViewModel.resetDetailViewMovement();
    }
}
