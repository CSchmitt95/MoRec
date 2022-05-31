package de.carloschmitt.morec.repository.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.Constants;

public class Sensor {
    private static final String TAG = "Sensor";
    private MoRecRepository moRecRepository;

    //UI STUFF
    private MutableLiveData<String> live_name;
    private MutableLiveData<String> live_address;
    private MutableLiveData<Boolean> live_paired;
    private MutableLiveData<Boolean> live_connected;
    private HashMap<Integer, MutableLiveData<Integer>> live_SampleCounters;


    //Connection Stuff
    private TssMiniBluetooth tssMiniBluetooth;

    //Recording Stuff
    private ArrayList<Sample> recordBuffer;

    //Sensor Health Stuff
    private int current_num_of_duplicates;
    private int max_num_of_duplicates;
    private MutableLiveData<String> live_sensor_health;
    private HashMap<Integer, Integer> sampleCounters;

    public Sensor(String new_name, String new_address){
        moRecRepository = MoRecRepository.getInstance();
        this.live_name = new MutableLiveData<>(new_name);
        this.live_address = new MutableLiveData<>(new_address);
        live_paired = new MutableLiveData<>(false);
        live_connected = new MutableLiveData<>(false);

        tssMiniBluetooth = null;
        recordBuffer = new ArrayList<>();
        current_num_of_duplicates = 0;
        max_num_of_duplicates = current_num_of_duplicates;
        live_sensor_health = new MutableLiveData<>("---");
        sampleCounters = new HashMap<>();
        live_SampleCounters = new HashMap<>();
    }

    public void createConnection() throws TssConnectionException, TssCommunicationException {
        tssMiniBluetooth = new TssMiniBluetooth(live_address.getValue(), true);
        tssMiniBluetooth.startStream();
        current_num_of_duplicates = 0;
        max_num_of_duplicates = 0;
    }

    public void destroyConnection() throws TssConnectionException, TssCommunicationException {
        tssMiniBluetooth.stopStream();
        tssMiniBluetooth.disconnectSocket();
        resetDuplicateQuaternions();
    }

    public void recordQuaternion() throws TssCommunicationException {
        int label_id = moRecRepository.getCurrentRecordingLabel();

        recordBuffer.add(new Sample(tssMiniBluetooth.getOrientationAsQuaternion(), moRecRepository.getCurrentRecordingLabel()));
        if (checkForNewDuplicate())
            increaseDuplicateQuaternions();
        else resetDuplicateQuaternions();

        if (sampleCounters.get(label_id) == null) {
            sampleCounters.put(label_id, 1);
            live_SampleCounters.put(label_id, new MutableLiveData<>(1));
        }
        sampleCounters.put(label_id, sampleCounters.get(label_id) + 1);
        live_SampleCounters.get(label_id).postValue(sampleCounters.get(label_id));
    }

    private boolean checkForNewDuplicate(){
        if(recordBuffer.size() < 3) return false;
        return recordBuffer.get(recordBuffer.size()-1).getQuaternion().equals(recordBuffer.get(recordBuffer.size()-2).getQuaternion());
    }

    public MutableLiveData<Integer> getNumberOfSamplesForUI(int label_id){
        if (live_SampleCounters.get(label_id) == null) {
            sampleCounters.put(label_id, 0);
            live_SampleCounters.put(label_id, new MutableLiveData<>(sampleCounters.get(label_id)));

        }
        return live_SampleCounters.get(label_id);
    }

    /**
     * Classification Stuff
     *
     */

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

    /**
     * Export stuff
     */

    public ArrayList<Sample> getRecordBuffer() {
        return recordBuffer;
    }

    /**
     * SensorHealth Stuff
     */

    private void increaseDuplicateQuaternions(){
        current_num_of_duplicates++;
        if(current_num_of_duplicates > max_num_of_duplicates) max_num_of_duplicates = current_num_of_duplicates;
        live_sensor_health.postValue(Integer.toString(max_num_of_duplicates));
    }

    private void resetDuplicateQuaternions() {
        current_num_of_duplicates = 0;
    }

    public boolean died(){
        return current_num_of_duplicates > Constants.SENSOR_HEALTH_THRESHHOLD;
    }

    /**
     * Getters and Setters
     */


    public LiveData<Boolean> getLive_paired() {
        return live_paired;
    }

    public LiveData<Boolean> getLive_connected() {
        return live_connected;
    }

    public MutableLiveData<String> getLive_name() {
        return live_name;
    }


    public MutableLiveData<String> getLive_address() {
        return live_address;
    }

    public void setLive_address(MutableLiveData<String> new_address) {
        this.live_address = new_address;
    }

    public void setPaired() {
        this.live_paired.postValue(true);
    }

    public void setConnected() {
        this.live_connected.postValue(true);
    }

    public void setDisconnected(){ this.live_connected.postValue(false); }

    public MutableLiveData<String> getLive_sensor_health() {
        return live_sensor_health;
    }

}
