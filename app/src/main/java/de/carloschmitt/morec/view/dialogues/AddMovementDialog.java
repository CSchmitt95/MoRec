package de.carloschmitt.morec.view.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import de.carloschmitt.morec.model.recording.UIMovement;
import de.carloschmitt.morec.viewmodel.MovementListViewModel;

public class AddMovementDialog extends DialogFragment {
    private static final String TAG = "AddNewMovementDialog";

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        MovementListViewModel movementListViewModel = new ViewModelProvider(getActivity()).get(MovementListViewModel.class);


        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setTitle("Neue Bewegung hinzufügen");
        alertDialogBuilder.setPositiveButton("Hinzufügen",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input_text = input.getText().toString();
                if( input_text != null && !input_text.trim().isEmpty()) {
                    UIMovement uiMovement = new UIMovement();
                    uiMovement.setName(input_text);
                    movementListViewModel.addMovement(uiMovement);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen",null);

        return alertDialogBuilder.create();
    }
}
