package de.carloschmitt.morec.view.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.checkerframework.checker.guieffect.qual.UI;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.DialogSensorDetailsBinding;
import de.carloschmitt.morec.repository.model.UISensor;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;

public class SensorDialogue extends DialogFragment {
    private static final String TAG = "SensorDialogue";
    SetupPageViewModel setupPageViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sensor_details, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogSensorDetailsBinding binding = DialogSensorDetailsBinding.inflate(LayoutInflater.from(getContext()),null, false);
        setupPageViewModel = new ViewModelProvider(getActivity()).get(SetupPageViewModel.class);
        Log.d(TAG, "setupPageViewModel Instance: "  + setupPageViewModel.toString());
        UISensor uiSensor = setupPageViewModel.getSelectedSensor().getValue();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        if (uiSensor == null) {
            alertDialogBuilder.setTitle("Sensor hinzufügen");
            uiSensor = new UISensor("Neuer Sensor", "00:00:00:00:00:00");
            setupPageViewModel.setSelectedSensor(uiSensor);
        }
        else {
            alertDialogBuilder.setTitle("Sensor bearbeiten");
        }
        binding.setUiSensor(uiSensor);
        alertDialogBuilder.setView(binding.getRoot());
        return alertDialogBuilder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        setupPageViewModel.resetSelectedSensor();
    }
}
