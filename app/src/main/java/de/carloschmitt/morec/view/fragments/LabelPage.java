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

import de.carloschmitt.morec.databinding.PageRecordBinding;
import de.carloschmitt.morec.viewmodel.LabelPageViewModel;
import de.carloschmitt.morec.viewmodel.adapters.LabelAdapter;

public class LabelPage extends Fragment {
    private static final String TAG = "LabelPage";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PageRecordBinding binding = PageRecordBinding.inflate(inflater);

        LabelPageViewModel labelPageViewModel = new ViewModelProvider(getActivity()).get(LabelPageViewModel.class);
        binding.setLabelPageViewModel(labelPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //RecylcerView
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        LabelAdapter labelAdapter = new LabelAdapter(labelPageViewModel);
        labelPageViewModel.getLabelList().observe(getViewLifecycleOwner(), list -> labelAdapter.submitList(new ArrayList<>(list)));
        binding.list.setAdapter(labelAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(labelPageViewModel.getExportButtonEnabled().getValue()){
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    labelPageViewModel.removeLabel(labelAdapter.getCurrentList().get(viewHolder.getBindingAdapterPosition()));
                                    Toast.makeText(getActivity(), "Label entfernt", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    labelAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Soll das Label wirklich entfernt werden?").setPositiveButton("Ja", dialogClickListener)
                            .setNegativeButton("Nein", dialogClickListener).show();
                }
                else{
                    Toast.makeText(getActivity(), "Label kann gerade nicht entfernt werden", Toast.LENGTH_SHORT).show();
                    labelAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                }
            }
        }).attachToRecyclerView(binding.list);

        return binding.getRoot();
    }

}
