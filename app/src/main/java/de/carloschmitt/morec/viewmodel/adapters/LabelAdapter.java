package de.carloschmitt.morec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ItemLabelBinding;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.viewmodel.LabelPageViewModel;

public class LabelAdapter extends ListAdapter<Label, RecyclerView.ViewHolder> {
    private static final String TAG = "UIMovementAdapter";
    LabelPageViewModel labelPageViewModel;

    public LabelAdapter(LabelPageViewModel labelPageViewModel){
        super(DIFF_CALLBACK);
        this.labelPageViewModel = labelPageViewModel;
    }

    public static final DiffUtil.ItemCallback<Label> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Label>() {
                @Override
                public boolean areItemsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
                    return oldItem.getLabel_id().getValue() == newItem.getLabel_id().getValue();
                }
                @Override
                public boolean areContentsTheSame(@NonNull Label oldItem, @NonNull Label newIte) {
                    return true;
                }
            };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLabelBinding binding = ItemLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.setLifecycleOwner((LifecycleOwner) parent.getContext());
        BindableViewholder bindableViewholder = new BindableViewholder(binding);
        bindableViewholder.itemLabelBinding.cvMovement.setOnClickListener(bindableViewholder);
        return bindableViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BindableViewholder) holder).itemLabelBinding.setLabel(getItem(position));
    }

    public class BindableViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemLabelBinding itemLabelBinding;

        BindableViewholder(ItemLabelBinding binding) {
            super(binding.getRoot());
            this.itemLabelBinding = binding;
        }

        @Override
        public void onClick(View v) {
            labelPageViewModel.setDetailViewMovement(itemLabelBinding.getLabel());
            Navigation.findNavController(v).navigate(R.id.open_detail_movement);
        }
    }
}
