package de.carloschmitt.morec.repository.model;

import android.util.Log;

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

    //UI STUFF
    private MutableLiveData<String> live_name;
    private MutableLiveData<String> live_address;
    private MutableLiveData<Boolean> live_paired;
    private MutableLiveData<Boolean> live_connected;
    private HashMap<Integer, MutableLiveData<Integer>> ui_SampleCounters;
    private MutableLiveData<String> live_sensor_health;


    //FUNCTIONAL STUFF
    private String name;
    private String address;
    private MoRecRepository moRecRepository;
    private TssMiniBluetooth tssMiniBluetooth;
    private ArrayList<Sample> recordBuffer;
    private int num_of_duplicates;
    private int max_num_of_duplicates;
    private HashMap<Integer, Integer> sampleCounters;

    public Sensor(String new_name, String new_address){
        moRecRepository = MoRecRepository.getInstance();
        this.name = new_name;
        this.live_name = new MutableLiveData<>(name);
        this.address = new_address;
        this.live_address = new MutableLiveData<>(address);
        live_paired = new MutableLiveData<>(false);
        live_connected = new MutableLiveData<>(false);

        tssMiniBluetooth = null;
        recordBuffer = new ArrayList<>();
        num_of_duplicates = 0;
        max_num_of_duplicates = num_of_duplicates;
        live_sensor_health = new MutableLiveData<>("---");
        sampleCounters = new HashMap<>();
        ui_SampleCounters = new HashMap<>();
    }

    public void createConnection() throws TssConnectionException, TssCommunicationException {
        tssMiniBluetooth = new TssMiniBluetooth(live_address.getValue(), true);
        tssMiniBluetooth.startStream();
        num_of_duplicates = 0;
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
            ui_SampleCounters.put(label_id, new MutableLiveData<>(1));
        }
        sampleCounters.put(label_id, sampleCounters.get(label_id) + 1);
        ui_SampleCounters.get(label_id).postValue(sampleCounters.get(label_id));
    }

    private boolean checkForNewDuplicate(){
        if(recordBuffer.size() < 3) return false;
        return recordBuffer.get(recordBuffer.size()-1).getQuaternion().equals(recordBuffer.get(recordBuffer.size()-2).getQuaternion());
    }

    public MutableLiveData<Integer> getNumberOfSamplesForUI(int label_id){
        if (ui_SampleCounters.get(label_id) == null) {
            sampleCounters.put(label_id, 0);
            ui_SampleCounters.put(label_id, new MutableLiveData<>(sampleCounters.get(label_id)));

        }
        return ui_SampleCounters.get(label_id);
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
        num_of_duplicates++;
        if(num_of_duplicates > max_num_of_duplicates) max_num_of_duplicates = num_of_duplicates;
        live_sensor_health.postValue(Integer.toString(max_num_of_duplicates));
    }

    private void resetDuplicateQuaternions() {
        num_of_duplicates = 0;
    }

    public LiveData<Boolean> getLive_paired() {
        return live_paired;
    }

    public LiveData<Boolean> getLive_connected() {
        return live_connected;
    }

    public MutableLiveData<String> getLive_name() {
        return live_name;
    }

    public void setLive_name(MutableLiveData<String> new_name) {
        this.name = new_name.getValue();
        this.live_name.postValue(name);
    }

    public MutableLiveData<String> getLive_address() {
        return live_address;
    }

    public void setLive_address(MutableLiveData<String> new_address) {
        this.address = new_address.getValue();
        this.live_address.postValue(address);
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

    public String getName() {
        return name;
    }

    public boolean died(){
        return num_of_duplicates > Constants.SENSOR_HEALTH_THRESHHOLD;
    }
}
