package de.carloschmitt.morec.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.FragmentItemMovementBinding;
import de.carloschmitt.morec.model.recording.UIMovement;
import de.carloschmitt.morec.viewmodel.MovementListViewModel;

public class UIMovementAdapter extends RecyclerView.Adapter<UIMovementAdapter.ViewHolder> {
    private static final String TAG = "UIMovementAdapter";
    ArrayList<UIMovement> uiMovements;
    MovementListViewModel movementListViewModel;

    public class ViewHolder extends RecyclerView.ViewHolder {
        FragmentItemMovementBinding binding;

        public ViewHolder(FragmentItemMovementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public UIMovementAdapter(ArrayList<UIMovement> uiMovements, MovementListViewModel movementListViewModel) {
        this.uiMovements = uiMovements;
        this.movementListViewModel = movementListViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UIMovementAdapter.ViewHolder(FragmentItemMovementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UIMovement uiMovement = uiMovements.get(position);
        holder.binding.movementName.setText(uiMovement.getName());
        holder.binding.movementCount.setText(Integer.toString(uiMovement.getSec_counter()));
        holder.binding.cvMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementListViewModel.setDetailViewMovement(holder.getAbsoluteAdapterPosition());
                Navigation.findNavController(holder.binding.getRoot()).navigate(R.id.open_detail_movement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uiMovements.size();
    }
}
