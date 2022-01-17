package de.carloschmitt.morec.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;
import de.carloschmitt.morec.pages.MovementPage;

public class MovementDialog extends DialogFragment {
    private static final String TAG = "MovementDialogFragment";
    static Movement movement;
    static Button btn_record;
    static TextView sample_count;
    static TextView status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_movement, container);
    }

    public static MovementDialog newInstance(Movement mp) {
        MovementDialog frag = new MovementDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setCancelable(false);
        movement = mp;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MovementPage mpf = (MovementPage) getParentFragment();


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_movement, null);

        EditText editTextMovementName = view.findViewById(R.id.editTextMovementName);
        editTextMovementName.setText(movement.getName());

        sample_count = view.findViewById(R.id.tv_sampleCount);
        sample_count.setText(movement.getRecordingCount());

        CheckBox isEvent = view.findViewById(R.id.cb_event);
        isEvent.setChecked(movement.isSingle_window());

        btn_record = view.findViewById(R.id.btn_record2);
        btn_record.setEnabled(Data.state == Data.State.CONNECTED);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.state == Data.State.CONNECTED) {
                    btn_record.setText("Aufnahme stoppen");
                    Data.startRecording(movement);
                }
                else if(Data.state == Data.State.RECORDING){
                    Data.stopRecording();
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Bewegungsdetails");
        alertDialogBuilder.setPositiveButton("Speichern",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                movement.setName(editTextMovementName.getText().toString());
                movement.setSingle_window(isEvent.isChecked());
                if(!Data.movements.contains(movement)) Data.movements.add(movement);
                Data.movementItemAdapter.notifyItemChanged(Data.movements.indexOf(movement));
                if(Data.state == Data.State.RECORDING) Data.stopRecording();
            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Data.movements.contains(movement)) Data.movementItemAdapter.notifyItemChanged(Data.movements.indexOf(movement));
                if(Data.state == Data.State.RECORDING) Data.stopRecording();
            }
        });

        alertDialogBuilder.setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Data.movements.contains(movement)){
                    int pos = Data.movements.indexOf(movement);
                    Data.movements.remove(movement);
                    Data.movementItemAdapter.notifyItemRemoved(pos);Data.movementItemAdapter.notifyItemChanged(Data.movements.indexOf(movement));
                }
                if(Data.state == Data.State.RECORDING) Data.stopRecording();
            }
        });

        status = view.findViewById(R.id.tv_status);

        alertDialogBuilder.setView(view);
        return alertDialogBuilder.create();
    }


    public static void refreshTexts(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Data.state == Data.State.RECORDING)  btn_record.setText("Aufnahme stoppen");
                else                                    btn_record.setText("Aufnahme starten");
                if(Data.state == Data.State.INACTIVE || Data.state == Data.State.CONNECTING) btn_record.setEnabled(false);
                else                                                                         btn_record.setEnabled(true);

                sample_count.setText(movement.getRecordingCount());
                status.setText("");
            }
        }, 100);
    }

    public static void showStatusText(int timeInS){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status.setText(timeInS + " Sek");
            }
        }, 100);
    }
}
