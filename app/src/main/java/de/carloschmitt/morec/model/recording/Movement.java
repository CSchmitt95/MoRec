package de.carloschmitt.morec.model.recording;


import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.model.setup.Sensor;

public  class Movement {
    private static final String TAG = "Movement";
    public String label;
    public boolean single_window;
    private List<Recording> recordings;
    Map<Sensor, Recording> currentRecordings;
    int currentRecordingSize;
    int currentRecordId;

    public Movement(String label, boolean single_window) {
        this.recordings = new LinkedList<>();
        this.single_window = single_window;
        this.label = label;
        currentRecordings = new HashMap<>();
        currentRecordingSize = 0;
        currentRecordId = 0;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRecordingCount() {
        if(single_window) return recordings.size() + " Fenster";
        else {
            List<Integer> done = new LinkedList<>();
            int sum_samples = 0;
            for(Recording recording : recordings){
                if(!done.contains(recording.getSession_id())) {
                    sum_samples += recording.getSize();
                    done.add(recording.getSession_id());
                }
            }
            return (sum_samples/ ApplicationController.SAMPLES_PER_SECOND) + " Sekunden (" + recordings.size() + " Aufnahmen)";
        }
        //return tapes.size() + " (" + (int) Math.ceil(tapes.size()/(Data.SAMPLES_PER_SECOND*Data.WINDOW_SIZE_IN_S)) +" unique)";
    }

    @Override
    public String toString() {
        return label;
    }


    public boolean isSingle_window() {
        return single_window;
    }

    public void setSingle_window(boolean single_window) {
        this.single_window = single_window;
    }

    public boolean addSamples(Map<Sensor, Quaternion> samples){
        if(currentRecordingSize == 0) {
            Log.d(TAG, "Neue Samplemap angefangen...");
            for(Sensor s : samples.keySet()){
                currentRecordings.put(s, new Recording(this, s, currentRecordId));
            }
        } else {
            for(Sensor s : samples.keySet()){
                currentRecordings.get(s).addQuaternion(samples.get(s));
            }
        }
        currentRecordingSize++;
        //if(currentRecordingSize % ApplicationController.SAMPLES_PER_SECOND == 0 ) RecordingDialogue.showStatusText(currentRecordingSize/ ApplicationController.SAMPLES_PER_SECOND);

        if(single_window && currentRecordingSize == ApplicationController.MAX_SAMPLES) {
            return true;
        }
        return false;
    }

    public void finishCurrentRecordings(){
        Log.d(TAG, "Recording beendet mit größe " + currentRecordingSize);
        for(Sensor s : currentRecordings.keySet()){
            recordings.add(currentRecordings.get(s));
        }
        currentRecordingSize = 0;
        currentRecordId++;
        currentRecordings = new HashMap<>();
    }

    public List<Recording> getRecordings(){
        return recordings;
    }
}