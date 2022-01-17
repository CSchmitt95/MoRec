package de.carloschmitt.morec.adapters;

import android.app.Fragment;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.carloschmitt.morec.databinding.FragmentItemMovementBinding;
import de.carloschmitt.morec.databinding.FragmentItemSensorBinding;
import de.carloschmitt.morec.model.Movement;
import de.carloschmitt.morec.model.Sensor;

public class SensorItemAdapter extends RecyclerView.Adapter<SensorItemAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Sensor sensor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "SensorItemAdapter";
        public final TextView tv_sensor_name;
        public final TextView tv_sensor_address;
        public final CheckBox cb_paired;
        public final CheckBox cb_active;
        public final CardView cv_sensor;
        public Sensor sensor;

        public ViewHolder(FragmentItemSensorBinding binding) {
            super(binding.getRoot());
            tv_sensor_name = binding.sensorName;
            tv_sensor_address = binding.sensorAddress;
            cv_sensor = binding.cvSensor;
            cb_paired = binding.chkPaired;
            cb_active = binding.chkActive;
        }

        public void bind(final Sensor item, final SensorItemAdapter.OnItemClickListener listener) {
            tv_sensor_name.setText(item.getName());
            cb_paired.setChecked(item.isPaired());
            cb_active.setChecked(item.isActive());
            tv_sensor_address.setText(item.getAddress());
            sensor = item;

            cv_sensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(sensor);
                }
            });
        }
    }


    private final List<Sensor> sensorList;
    private final SensorItemAdapter.OnItemClickListener listener;

    public SensorItemAdapter(List<Sensor> sensors, SensorItemAdapter.OnItemClickListener listener){
        this.sensorList = sensors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SensorItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SensorItemAdapter.ViewHolder(FragmentItemSensorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SensorItemAdapter.ViewHolder holder, int position) {
        holder.bind(sensorList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }



}
