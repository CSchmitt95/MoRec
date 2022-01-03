package de.carloschmitt.morec.model;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.ArrayList;
import java.util.List;


/**
 * Auf Ein Tape werden Quaternionen aufgenommen.
 * Ein Tape gehört zu einem Sensor und einer Bewegung.
 * Ein Tape hat eine maximale größe, ist diese 0, so wird zeichnet es auf bis es gestoppt wird.
 * Ein Tape kann sich automatisch neu starten. Dazu wird der Overlap verwendet.
 */
public class Tape {
    private static final String TAG = "Tape";

    private Sensor sensor;
    private int maxSize;
    private MovementPattern movementPattern;
    private List<Quaternion> quaternions;
    private boolean autorestart;


    /**
     * Erstellt ein neues Tape.
     * @param sensor gibt den Sensor an, der für die Aufnahme der Daten verwendet sein soll.
     * @param movementPattern gibt das MovementPattern an, dem die Aufnahmen zugeordnet werden sollen.
     */
    public Tape(Sensor sensor, MovementPattern movementPattern){
        quaternions = new ArrayList<>();
        this.sensor = sensor;
        this.movementPattern = movementPattern;
        if(movementPattern.isSingle_window()){
            maxSize = Data.WINDOW_SIZE_IN_S* Data.SAMPLES_PER_SECOND;
            autorestart = false;
        }
        else {
            maxSize = 0;
            autorestart = false;
        }
    }

    public void addQuaternion(Quaternion q) {
        quaternions.add(q);
    }

    public boolean shouldBeRestarted() {
        return quaternions.size() == maxSize - (Data.OVERLAP_IN_S*Data.SAMPLES_PER_SECOND);
    }

    public boolean isFull(){
        //Log.d(TAG,"checke ob voll : " + quaternions.size() + "/" + (Data.SAMPLES_PER_SECOND*Data.WINDOW_SIZE_IN_S));
        if(maxSize == 0) return false;
        return quaternions.size() == Data.SAMPLES_PER_SECOND*Data.WINDOW_SIZE_IN_S;
    }
    public String getQuaternionsAsSting(){
        String ret = "";
        for(Quaternion q : quaternions){
            ret += q.getW()+ "," + q.getX() + "," +q.getY() + "," + q.getZ()+ ",";
        }
        return ret;
    }

    public String getQuaternionsAsNulledString(){
        String ret = "";
        Quaternion last = null;
        for(Quaternion q : quaternions){
            if(last == null) ret+= "0,0,0,0,";
            else{
                ret += (q.getW()-last.getW())+ "," + (q.getX()-last.getX()) + "," +(q.getY()-last.getY()) + "," + (q.getZ()-last.getZ())+ ",";
            }
            //ret += q.getW()+ "," + q.getX() + "," +q.getY() + "," + q.getZ()+ ",";
            last = q;
        }
        return ret;
    }
   @Override
    public String toString(){
        return "Tapeinfo::  Bewegung: " + movementPattern.getName() + " Sensor: " + sensor + " (" + quaternions.size() + "/" + maxSize + ")";
    }

    public int getSize(){
        return quaternions.size();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }
}
