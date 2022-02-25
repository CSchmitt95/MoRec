package de.carloschmitt.morec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.carloschmitt.morec.databinding.FragmentItemMovementBinding;
import de.carloschmitt.morec.model.recording.Movement;

public class MovementItemAdapter extends RecyclerView.Adapter<MovementItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Movement movementpattern);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tv_movement_name;
        public final TextView tv_movement_samplecount;
        public final CardView cv_movement;
        public Movement movement;

        public ViewHolder(FragmentItemMovementBinding binding) {
            super(binding.getRoot());
            tv_movement_name = binding.movementName;
            tv_movement_samplecount = binding.movementCount;
            cv_movement = binding.cvMovement;
        }

        public void bind(final Movement item, final OnItemClickListener listener) {
            tv_movement_name.setText(item.getLabel());
            tv_movement_samplecount.setText(item.getRecordingCount());
            movement = item;
            cv_movement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movement);
                }
            });
        }
    }

    private final List<Movement> movementList;
    private final OnItemClickListener listener;

    public MovementItemAdapter(List<Movement> movements, OnItemClickListener listener){
        this.movementList = movements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovementItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovementItemAdapter.ViewHolder(FragmentItemMovementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovementItemAdapter.ViewHolder holder, int position) {
        holder.bind(movementList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }


}
