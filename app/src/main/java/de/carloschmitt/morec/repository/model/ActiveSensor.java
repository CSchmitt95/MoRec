package de.carloschmitt.morec.repository.model;

import androidx.lifecycle.MutableLiveData;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.carloschmitt.morec.repository.util.Constants;
import de.carloschmitt.morec.repository.MoRecRepository;

public class ActiveSensor {
    private MoRecRepository moRecRepository;
    private String name;
    private TssMiniBluetooth tssMiniBluetooth;
    private ArrayList<Sample> recordBuffer;
    private HashMap<Integer, Integer> label_counters;
    private HashMap<Integer, MutableLiveData<Integer>> label_counters_ui;
    private int duplicateQuaternions;
    private MutableLiveData<String> sensorHealth;

    public ActiveSensor(String name, String address) throws TssConnectionException, TssCommunicationException {
        this.name = name;
        tssMiniBluetooth = new TssMiniBluetooth(address, true);
        //tssMiniBluetooth.setCurrentOrientationAsTare();
        tssMiniBluetooth.startStream();
        moRecRepository = MoRecRepository.getInstance();
        recordBuffer = new ArrayList<>();
        label_counters = new HashMap<>();
        label_counters_ui = new HashMap<>();
        duplicateQuaternions = 0;
        sensorHealth = new MutableLiveData<>("");
    }

    public String getName() {
        return name;
    }

    public void destroy(){
        try{
            tssMiniBluetooth.stopStream();
            tssMiniBluetooth.disconnectSocket();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void recordQuaternion() throws TssCommunicationException {
        int label_id = moRecRepository.getCurrentRecordingLabel();

        recordBuffer.add(new Sample(tssMiniBluetooth.getOrientationAsQuaternion(), moRecRepository.getCurrentRecordingLabel()));
        if(recordBuffer.get(recordBuffer.size()).equals(recordBuffer.get(recordBuffer.size()-1))) increaseDuplicateQuaternions();
        else resetDuplicateQuaternions();

        if(label_counters.get(label_id) == null){
            label_counters.put(label_id,1);
            label_counters_ui.put(label_id, new MutableLiveData<>(1));
        }
        label_counters.put(label_id, label_counters.get(label_id)+1);
        label_counters_ui.get(label_id).postValue(label_counters.get(label_id));
    }

    public int getNumberOfSampelesFor(int label_id){
        if (label_counters.get(label_id) == null) return 0;
        return label_counters.get(label_id);
    }

    public MutableLiveData<Integer> getNumberOfSamplesForUI(int label_id){
        if (label_counters_ui.get(label_id) == null) {
            label_counters.put(label_id, 0);
            label_counters_ui.put(label_id, new MutableLiveData<>(label_counters.get(label_id)));

        }
        return label_counters_ui.get(label_id);
    }

    public boolean bufferIsSaturated(){
        return recordBuffer.size() > Constants.SAMPLES_PER_SECOND*Constants.WINDOW_SIZE_IN_S +1;
    }

    public List<Quaternion> getLastNQuaternions(int n){
        if (n > recordBuffer.size()) return null;
        List<Sample> lastN = new ArrayList<>(recordBuffer.subList(recordBuffer.size()-n-1, recordBuffer.size()-1));
        List<Quaternion> ret = new ArrayList<>();
        for(Sample sample : lastN){
            ret.add(sample.getQuaternion());
        }
        return ret;
    }

    public ArrayList<Sample> getRecordBuffer() {
        return recordBuffer;
    }

    private void increaseDuplicateQuaternions(){
        duplicateQuaternions++;
        sensorHealth.postValue(Integer.toString(duplicateQuaternions));
    }

    private void resetDuplicateQuaternions() {
        duplicateQuaternions = 0;
        sensorHealth.postValue(Integer.toString(duplicateQuaternions));
    }
}
