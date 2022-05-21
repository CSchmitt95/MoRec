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

import de.carloschmitt.morec.databinding.DialogLabelDetailsBinding;
import de.carloschmitt.morec.view.activities.MainActivity;
import de.carloschmitt.morec.viewmodel.DetailedLabelDialogViewModel;
import de.carloschmitt.morec.viewmodel.LabelPageViewModel;

public class DetailedLabelDialogue extends DialogFragment {
    private final String TAG = "DetailedLabelDialogue";
    LabelPageViewModel labelPageViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogLabelDetailsBinding binding = DialogLabelDetailsBinding.inflate(LayoutInflater.from(getContext()),null, false);

        labelPageViewModel = new ViewModelProvider(getActivity()).get(LabelPageViewModel.class);

        DetailedLabelDialogViewModel detailedLabelDialogViewModel = new ViewModelProvider(this).get(DetailedLabelDialogViewModel.class);
        detailedLabelDialogViewModel.setUiLabel(labelPageViewModel.getDetailViewMovement());
        binding.setDetailedLabelDialogViewModel(detailedLabelDialogViewModel);

        binding.setLifecycleOwner(getActivity());


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        builder.setTitle("Aufzeichnung");
        /*builder.setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                labelPageViewModel.removeLabel(labelPageViewModel.getDetailViewMovement());
            }
        });*/

        //builder.setPositiveButton("Schliessen",  null);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        labelPageViewModel.resetDetailViewMovement();
    }
}
