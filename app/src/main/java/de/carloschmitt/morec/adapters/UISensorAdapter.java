package de.carloschmitt.morec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.carloschmitt.morec.databinding.FragmentItemSensorBinding;
import de.carloschmitt.morec.model.setup.UISensor;

public class UISensorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<UISensor> uiSensorArrayList;

    public UISensorAdapter(){
        this.uiSensorArrayList = new ArrayList<>();
    }

    public void updateList(ArrayList<UISensor> new_list){
        this.uiSensorArrayList.clear();
        this.uiSensorArrayList = new_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UISensorViewHolder(FragmentItemSensorBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UISensor uiSensor = uiSensorArrayList.get(position);
        UISensorViewHolder uiSensorViewHolder = (UISensorViewHolder) holder;
        uiSensorViewHolder.binding.sensorName.setText(uiSensor.getName());
        uiSensorViewHolder.binding.sensorAddress.setText(uiSensor.getAddress());
        uiSensorViewHolder.binding.chkPaired.setChecked(uiSensor.isPaired());
        uiSensorViewHolder.binding.chkActive.setChecked(uiSensor.isConnected());
    }

    @Override
    public int getItemCount() {
        return uiSensorArrayList.size();
    }

    class UISensorViewHolder extends RecyclerView.ViewHolder {
        FragmentItemSensorBinding binding;
        public UISensorViewHolder(FragmentItemSensorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
