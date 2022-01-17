package de.carloschmitt.morec.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.adapters.MovementItemAdapter;
import de.carloschmitt.morec.dialogs.MovementDialog;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;

public class MovementPage extends Fragment implements View.OnClickListener {
    private static final String TAG = "MovementPageFragment";
    public RecyclerView rv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_movement, container, false);
        Log.d(TAG,"TEST");

        rv = view.findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(Data.movementItemAdapter == null) {
            Data.movementItemAdapter = new MovementItemAdapter(Data.movements, new MovementItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Movement movementpattern) {
                    openMovementPatternDialog(movementpattern);
                }
            });
        }
        rv.setAdapter(Data.movementItemAdapter);


        Button btn_addMovement = view.findViewById(R.id.btn_AddMovement);
        btn_addMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMovementPatternDialog(new Movement("",false));
            }
        });

        Button btn_exportData = view.findViewById(R.id.btn_Export);
        btn_exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.exportData(getActivity().getApplicationContext());
            }
        });

        return view;
    }

    private void openMovementPatternDialog(Movement mp){
        Log.d(TAG, mp.toString());
        MovementDialog movementDialog = MovementDialog.newInstance(mp);
        movementDialog.setCancelable(false);
        movementDialog.show(getChildFragmentManager(), "fragment_movementdialog");
    }

    public void refresh(){
        Data.movementItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
