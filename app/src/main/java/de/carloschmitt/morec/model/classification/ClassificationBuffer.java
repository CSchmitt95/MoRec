package de.carloschmitt.morec.model.classification;


import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.setup.Sensor;

public class ClassificationBuffer {
    private static final String TAG = "ClassificationBuffer";
    HashMap<Sensor, List<Quaternion>> data;
    HashMap<Sensor, Boolean> sensor_saturated;
    boolean saturated = false;

    public ClassificationBuffer(){
        data = new HashMap<>();
        sensor_saturated = new HashMap<>();
        for(Sensor s : ApplicationController.sensors){
            data.put(s, new LinkedList<>());
            sensor_saturated.put(s, false);
        }
    }

    public boolean addSample(HashMap<Sensor, Quaternion> new_data){
        Set<Sensor> new_sensors = new_data.keySet();
        for(Sensor s : new_sensors){
            List<Quaternion> qList = data.get(s);
            qList.add(new_data.get(s));
            if(sensor_saturated.get(s)) qList.remove(0);
            else {
                if ( qList.size() == ApplicationController.SAMPLES_PER_SECOND * ApplicationController.WINDOW_SIZE_IN_S + 1 ){
                    sensor_saturated.put(s, true);
                }
            }
        }
        return true;
    }

    public boolean isSaturated(){
        boolean ret = true;
        for(Sensor s : ApplicationController.sensors){
            if(sensor_saturated.get(s) == false) ret = false;
        }
        return ret;
    }


    public float[] getBuffer() {
        float[] ret = new float[ApplicationController.FLOATS_PER_WINDOW * ApplicationController.sensors.size()];
        String[] sensorOrder = {"Gürtel", "Handgelenk"};

        for(int s_count = 0; s_count < ApplicationController.sensors.size(); s_count++){
            String name = sensorOrder[s_count];
            Log.d(TAG,"Lese Daten von sensor " + name);
            List<Quaternion> sensor_data = null;

            for(Sensor data_s : ApplicationController.sensors){
                if(data_s.getName() == name) sensor_data = data.get(data_s);
            }
            if(sensor_data == null) {
                Log.e(TAG, "Sensordaten passen nicht!");
                return null;
            }

            List<Quaternion> diffQuaternions = new LinkedList<>();
            for (int i = 0; i < sensor_data.size() - 1; i++) {
                Quaternion current = sensor_data.get(i);
                Quaternion next = sensor_data.get(i + 1);
                Quaternion diff = next.mult(current.inverse());
                //Log.d("TAG", "Current" + current.toString() + " Zu next: " + next.toString());
                //Log.d("TAG", diff.toString());
                diffQuaternions.add(diff);
            }
            Log.d(TAG, "Size of diffbuffer: " + diffQuaternions.size());

            for(int j = 0; j < ApplicationController.FLOATS_PER_WINDOW; j++){
                if( j % 4 == 0) ret[s_count*1500 + j] = sensor_data.get((int) (j/4)).getW();
                else if( j % 4 == 1) ret[s_count*1500 + j] = sensor_data.get((int) (j/4)).getX();
                else if( j % 4 == 2) ret[s_count*1500 + j] = sensor_data.get((int) (j/4)).getY();
                else if( j % 4 == 3) ret[s_count*1500 + j] = sensor_data.get((int) (j/4)).getZ();
            }

            for (int j = 0; j < ApplicationController.SAMPLES_PER_SECOND * ApplicationController.WINDOW_SIZE_IN_S; j++) {
                ret[s_count*1500 + j * 4 + 0] = diffQuaternions.get(j).getW();
                ret[s_count*1500 + j * 4 + 1] = diffQuaternions.get(j).getX();
                ret[s_count*1500 + j * 4 + 2] = diffQuaternions.get(j).getY();
                ret[s_count*1500 + j * 4 + 3] = diffQuaternions.get(j).getZ();
            }

            for (int i = 1; i < ApplicationController.SAMPLES_PER_SECOND * ApplicationController.WINDOW_SIZE_IN_S; i++) {
                ret[s_count*1500 + i * 4 + 0] = ret[i * 4 + 0] - ret[0];
                ret[s_count*1500 + i * 4 + 1] = ret[i * 4 + 1] - ret[1];
                ret[s_count*1500 + i * 4 + 2] = ret[i * 4 + 2] - ret[2];
                ret[s_count*1500 + i * 4 + 3] = ret[i * 4 + 3] - ret[3];
            }

            ret[s_count*1500 + 0] = 0;
            ret[s_count*1500 + 1] = 0;
            ret[s_count*1500 + 2] = 0;
            ret[s_count*1500 + 3] = 0;

        }

        return ret;
    }


}
