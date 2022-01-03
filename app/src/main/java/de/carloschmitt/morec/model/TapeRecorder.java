package de.carloschmitt.morec.model;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.LinkedList;
import java.util.List;

/**
 * Der TapeRecorder handhabt die Aufnahme von Tapes.
 * Tapes können eingelegt werden.
 * Der Recorder muss gestartet werden um Auf die Tapes aufzuzeichnen.
 */
public class TapeRecorder {
    private final int TAPE_SIZE = Data.WINDOW_SIZE_IN_S*Data.SAMPLES_PER_SECOND;
    private boolean recording;
    private final String TAG = "MovementRecorder";
    List<Tape> currentTapes;


    List<Tape> toBeRemoved;
    List<Tape> newTapes;

    public TapeRecorder(){
        currentTapes = new LinkedList<>();
        recording = false;
        toBeRemoved = new LinkedList<>();
        newTapes = new LinkedList<>();
    }

    public void insertTape(Tape tape){
        currentTapes.add(tape);
        Log.d(TAG, "Neues Tape eingelegt. (Jetzt:" + currentTapes.size()+ ")");
    }

    private void insertNewTapes(){
        currentTapes.addAll(newTapes);
        Log.d(TAG,  newTapes.size() + " neue Tapes eingelegt. (Jetzt:" + currentTapes.size()+ ")");
        newTapes.clear();
    }

    private void removeFullTapes(){
        currentTapes.removeAll(toBeRemoved);
        Log.d(TAG,  toBeRemoved.size() + " volle Tapes ausgeworfen. (Jetzt:" + currentTapes.size()+ ")");
        toBeRemoved.clear();
    }

    public void recordQuaternion(Quaternion quaternion, Sensor sensor){
        if(!recording) return;

        //Log.d(TAG, "Anzahl tapes die aufgenommen werden:" + currentTapes.size());
        for(Tape tape : currentTapes){
            if(tape.getSensor() == sensor){
                //Log.d(TAG, "Quaternion gehört zu Sensor: " + sensor.getName());
                tape.addQuaternion(quaternion);
                if(tape.shouldBeRestarted()) newTapes.add(new Tape(tape.getSensor(), tape.getMovementPattern()));
                if(tape.isFull()) {
                    tape.getMovementPattern().tapes.add(tape);
                    toBeRemoved.add(tape);
                }
            }
        }
        if(!newTapes.isEmpty()) insertNewTapes();
        if(!toBeRemoved.isEmpty()) removeFullTapes();
    }

    public void discardAllTapes(){
        currentTapes.clear();
    }

    public void pressRecord(){
        if(recording) Log.e(TAG, "Aufnahme läuft bereits!!");
        this.recording = true;
    }

    public void pressStop(){
        if(!recording) Log.e(TAG, "Aufnahme bereits gestoppt");
        for (Tape tape : currentTapes){
            if(!tape.getMovementPattern().isSingle_window()){
                tape.getMovementPattern().tapes.add(tape);
            }
        }
        currentTapes.clear();
        this.recording = false;
    }

    public boolean isRecording() {
        return recording;
    }
}
