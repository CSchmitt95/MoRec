package de.carloschmitt.morec.recording;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.LinkedList;
import java.util.List;

import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;
import de.carloschmitt.morec.model.Sensor;

public class Recording {
    private static final String TAG = "Recording";

    private List<Quaternion> quaternions;
    private Movement movement;
    private Sensor sensor;

    int session_id;

    public Recording(Movement movement, Sensor sensor, int session_id){
        quaternions = new LinkedList<>();
        this.movement = movement;
        this.sensor = sensor;
        this.session_id = session_id;
    }

    public String getQuaternionsAsString(){
        String ret = "";
        for(Quaternion q : quaternions){
            ret += "," + q.getX() + "," +q.getY() + "," + q.getZ()+ "," + q.getW();
        }
        return ret;
    }

    public void addQuaternion(Quaternion q) {
        quaternions.add(q);
    }

    public boolean isFull(){
        if(movement.isSingle_window() && quaternions.size() == Data.MAX_SAMPLES) return true;
        return false;
    }

    public int getSize(){
        return quaternions.size();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Movement getMovement() {
        return movement;
    }

    public int getSession_id(){
        return session_id;
    }

}
