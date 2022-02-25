package de.carloschmitt.morec.view.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.setup.Sensor;
import de.carloschmitt.morec.view.SetupPage;

public class SetupDialogue extends DialogFragment {
    private static final String TAG = "MovementDialogFragment";
    static Sensor sensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_sensor, container);
    }

    public static SetupDialogue newInstance(Sensor s) {
        SetupDialogue frag = new SetupDialogue();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setCancelable(false);
        sensor = s;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SetupPage setupPage = (SetupPage) getParentFragment();
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_sensor, null);

        EditText editTextSensorName = view.findViewById(R.id.et_SensorName);
        editTextSensorName.setText(sensor.getName());

        EditText editTextSensorAddress = view.findViewById(R.id.et_SensorAddress);
        //editTextSensorAddress.setText(sensor.getAddress());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Sensordetails");
        alertDialogBuilder.setPositiveButton("Speichern",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sensor.setName(editTextSensorName.getText().toString());
                //sensor.setAddress(editTextSensorAddress.getText().toString());
                if(!ApplicationController.sensors.contains(sensor)) ApplicationController.sensors.add(sensor);
                ApplicationController.sensorItemAdapter.notifyItemChanged(ApplicationController.sensors.indexOf(sensor));
            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    ApplicationController.sensorItemAdapter.notifyItemChanged(ApplicationController.sensors.indexOf(sensor));
                }
            }
        });
        alertDialogBuilder.setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ApplicationController.sensors.contains(sensor)){
                    ApplicationController.sensors.remove(sensor);
                    ApplicationController.sensorItemAdapter.notifyDataSetChanged();
                }
            }
        });

        alertDialogBuilder.setView(view);
        return alertDialogBuilder.create();
    }

}
