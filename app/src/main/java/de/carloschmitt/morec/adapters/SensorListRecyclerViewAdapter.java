package de.carloschmitt.morec.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.carloschmitt.morec.databinding.FragmentSensorBinding;
import de.carloschmitt.morec.model.MovementPattern;
import de.carloschmitt.morec.model.Sensor;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MovementPattern}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SensorListRecyclerViewAdapter extends RecyclerView.Adapter<SensorListRecyclerViewAdapter.ViewHolder> {
    private final List<Sensor> sensorList;

    public SensorListRecyclerViewAdapter(List<Sensor> items) {
        sensorList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentSensorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Sensor si = sensorList.get(position);
        holder.sensor_name.setText(si.getName());
        holder.sensor_address.setText(si.getAddress());
        if(si.isPaired()) holder.sensor_name.setTextColor(Color.GREEN);
        if(si.isActive()) holder.sensor_name.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView sensor_name;
        public final TextView sensor_address;
        public final Button sensor_delete_button;

        public ViewHolder(FragmentSensorBinding binding) {
            super(binding.getRoot());
            sensor_name = binding.sensorName;
            sensor_address = binding.sensorAddress;
            sensor_delete_button = binding.btnDeleteSensor;
            sensor_delete_button.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + "";
        }

        @Override
        public void onClick(View v) {
            // Löschen aus Liste

        }
    }
}