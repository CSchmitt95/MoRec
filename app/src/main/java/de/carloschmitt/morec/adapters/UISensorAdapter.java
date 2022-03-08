package de.carloschmitt.morec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.carloschmitt.morec.R;
import de.carloschmitt.morec.databinding.ItemSensorBinding;
import de.carloschmitt.morec.repository.model.UISensor;
import de.carloschmitt.morec.view.activities.MainActivity;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;

public class UISensorAdapter extends ListAdapter<UISensor,RecyclerView.ViewHolder> {
    private final String TAG = "UISensorAdapter";
    SetupPageViewModel setupPageViewModel;

    public UISensorAdapter(SetupPageViewModel setupPageViewModel){
        super(DIFF_CALLBACK);
        this.setupPageViewModel = setupPageViewModel;
    }

    public static final DiffUtil.ItemCallback<UISensor> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UISensor>() {
                public String TAG = "DIFF_CALLBACK";

                @Override
                public boolean areItemsTheSame(@NonNull UISensor oldItem, @NonNull UISensor newItem) {
                    return oldItem.getName().getValue().equals(newItem.getName().getValue());
                }
                @Override
                public boolean areContentsTheSame(@NonNull UISensor oldItem, @NonNull UISensor newIte) {
                    return true;
                }
            };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSensorBinding binding = ItemSensorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.setLifecycleOwner((MainActivity) parent.getContext());
        BindableViewholder bindableViewholder = new BindableViewholder(binding);
        bindableViewholder.itemSensorBinding.cvSensor.setOnClickListener(bindableViewholder);
        return bindableViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BindableViewholder) holder).itemSensorBinding.setUiSensor(getItem(position));
    }

    public UISensor getUISensorAt(int pos){
        return getItem(pos);
    }

    public class BindableViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemSensorBinding itemSensorBinding;

        BindableViewholder(ItemSensorBinding binding) {
            super(binding.getRoot());
            this.itemSensorBinding = binding;
        }

        @Override
        public void onClick(View v) {
            UISensor clicked = itemSensorBinding.getUiSensor();
            setupPageViewModel.setSelectedSensor(clicked);
            Navigation.findNavController(v).navigate(R.id.open_setupDialogue);
        }
    }
}
