package de.carloschmitt.morec.viewmodel.adapters;

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
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.view.MainActivity;
import de.carloschmitt.morec.viewmodel.SetupPageViewModel;

public class SensorAdapter extends ListAdapter<Sensor,RecyclerView.ViewHolder> {
    private final String TAG = "UISensorAdapter";
    SetupPageViewModel setupPageViewModel;

    public SensorAdapter(SetupPageViewModel setupPageViewModel){
        super(DIFF_CALLBACK);
        this.setupPageViewModel = setupPageViewModel;
    }

    public static final DiffUtil.ItemCallback<Sensor> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Sensor>() {
                public String TAG = "DIFF_CALLBACK";

                @Override
                public boolean areItemsTheSame(@NonNull Sensor oldItem, @NonNull Sensor newItem) {
                    return oldItem.getLive_name().getValue().equals(newItem.getLive_name().getValue());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Sensor oldItem, @NonNull Sensor newIte) {
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
        ((BindableViewholder) holder).itemSensorBinding.setSensor(getItem(position));
    }

    public Sensor getUISensorAt(int pos){
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
            if(MoRecRepository.getInstance().getState().getValue() == State.INACTIVE){
                Sensor clicked = itemSensorBinding.getSensor();
                setupPageViewModel.setSelectedSensor(clicked);
                Navigation.findNavController(v).navigate(R.id.open_setupDialogue);
            }
        }
    }
}
