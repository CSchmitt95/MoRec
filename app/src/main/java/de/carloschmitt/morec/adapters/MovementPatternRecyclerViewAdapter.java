package de.carloschmitt.morec.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.carloschmitt.morec.activities.ListActivity;
import de.carloschmitt.morec.activities.RecordActivity;
import de.carloschmitt.morec.model.MovementPatternItem;
import de.carloschmitt.morec.databinding.FragmentItemBinding;
import de.carloschmitt.morec.model.MovementPatternList;

import java.util.List;
import java.util.ListIterator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MovementPatternItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MovementPatternRecyclerViewAdapter extends RecyclerView.Adapter<MovementPatternRecyclerViewAdapter.ViewHolder> {
    private final List<MovementPatternItem> movementPatternItems;

    public MovementPatternRecyclerViewAdapter(List<MovementPatternItem> items) {
        movementPatternItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.movementPatternItem = movementPatternItems.get(position);
        holder.tv_movement_name.setText(movementPatternItems.get(position).getName());
        holder.tv_movement_samplecount.setText(movementPatternItems.get(position).getSample_count());
    }

    @Override
    public int getItemCount() {
        return movementPatternItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tv_movement_name;
        public final TextView tv_movement_samplecount;
        public final Button recButton;
        public MovementPatternItem movementPatternItem;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            tv_movement_name = binding.movementName;
            tv_movement_samplecount = binding.movementSc;
            recButton = binding.btnRecordItem;

            recButton.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_movement_samplecount.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            ListActivity listActivity = (ListActivity) context;
            Intent intent = new Intent(context, RecordActivity.class);
            intent.putExtra("movement",movementPatternItem);
            intent.putExtra("Position", MovementPatternList.ITEMS.indexOf(movementPatternItem));
            listActivity.startActivityForResult(intent,1);

        }
    }
}