package de.carloschmitt.morec.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.ConfusionMatrix;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.Constants;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.runners.ConnectionRunner;

public class MoRecRepository {
    Context context;
    private static final String TAG = "MoRecRepository";
    private static MoRecRepository instance;

    private MutableLiveData<List<Label>> uiLabels;

    private List<Sensor> sensors;
    private MutableLiveData<List<Sensor>> uiSensors_ui;

    private State state;
    private MutableLiveData<State> state_ui;

    private int currentRecordingLabel;

    private MutableLiveData<String> classificationResult;

    private long last_classification;
    private CountDownLatch signalStop;
    private float[] current_actual;

    ConfusionMatrix cm_Grtl;
    ConfusionMatrix cm_Handgelenk;
    ConfusionMatrix cm_GrtlHandgelenk;

    int[] evaluation_label_counter;


    public MoRecRepository(){
        uiLabels = new MutableLiveData<>(new ArrayList<Label>());
        sensors = new ArrayList<>();
        uiSensors_ui = new MutableLiveData<>(sensors);
        state = State.INACTIVE;
        state_ui = new MutableLiveData<>(state);
        currentRecordingLabel = -1;
        classificationResult = new MutableLiveData<>("Noch kein Ergebnis");
        last_classification = System.currentTimeMillis();
        signalStop = null;
        current_actual = new float[] {0,0,0};
        evaluation_label_counter = new int[3];

        cm_Grtl = new ConfusionMatrix(3);
        cm_Handgelenk = new ConfusionMatrix(3);
        cm_GrtlHandgelenk = new ConfusionMatrix(3);
    }

    public static MoRecRepository getInstance() {
        if(instance == null){
            instance = new MoRecRepository();
            instance.init();
        }
        return instance;
    }

    public MutableLiveData<List<Label>> getUiLabels() {
        return uiLabels;
    }

    public MutableLiveData<List<Sensor>> getUiSensors_ui() {
        return uiSensors_ui;
    }

    public MutableLiveData<State> getState_ui() {
        return state_ui;
    }

    public MutableLiveData<String> getClassificationResult() {
        return classificationResult;
    }

    public void connectSensors(){
        if(state != State.INACTIVE) return;
        setState(State.CONNECTING);

        // Der Thread setzt bei Beendigung den Status auf CONNECTED ODER INACTIVE
        Thread connectionThread = new Thread(new ConnectionRunner());
        connectionThread.start();
    }

    public void disconnectSensors(){
        if(state != State.CONNECTED && state != State.CONNECTING) return;
        signalStop.countDown();
    }

    public void startRecording(int label_id){
        if(state == State.CONNECTED){
            currentRecordingLabel = label_id;
            setState(State.RECORDING);
        }
        else Log.d(TAG, "Something went wrong... were not connected...");
    }

    public void stopRecording(){
        if(state == State.RECORDING){
            setState(State.CONNECTED);
            currentRecordingLabel = -1;
        }
        else Log.d(TAG, "Something went wrong... We weren't recording...");
    }
    public void addLabel(Label label){
        List<Label> old_Labels = uiLabels.getValue();
        old_Labels.add(label);
        uiLabels.postValue(old_Labels);
    }

    public void removeLabel(Label label){
        List<Label> old_Labels = uiLabels.getValue();
        old_Labels.remove(label);
        uiLabels.postValue(old_Labels);
    }
    public void addUISensor(Sensor sensor){
        sensors.add(sensor);
        uiSensors_ui.postValue(sensors);
    }

    public void removeUISensor(Sensor sensor){
        sensors.remove(sensor);
        uiSensors_ui.postValue(sensors);
    }

    public void init(){
        uiSensors_ui.getValue().add(new Sensor("Guertel", "00:0E:0E:16:8F:F6"));
        uiSensors_ui.getValue().add(new Sensor("Handgelenk", "00:0E:0E:1B:60:DE"));

        uiLabels.getValue().add(new Label("Gehen"));
        uiLabels.getValue().add(new Label("Stehen"));
        uiLabels.getValue().add(new Label("Stolpern"));

        ConfusionMatrix pm = new ConfusionMatrix(3);
        float predicted[] = {0f,0.1f,0.9f};
        float actual[] = {0f,0f,1f};
        pm.addValue(predicted, actual);
    }

    public int getCurrentRecordingLabel() {
        return currentRecordingLabel;
    }

    public void setCurrentRecordingLabel(int currentRecordingLabel) {
        this.currentRecordingLabel = currentRecordingLabel;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        state_ui.postValue(state);
    }

    public void startClassifying(){
        if(state == State.CONNECTED) setState(State.CLASSIFYING);
    }

    public void stopClassifying(){
        if(state == State.CLASSIFYING) setState(State.CONNECTED);
    }

    public void setClassificationResult(String result){
        classificationResult.postValue(result);
    }

    public boolean checkClassificationCooldown(){
        long cooldown = System.currentTimeMillis() - last_classification;
        if(cooldown > Constants.CLASSIFICATION_COOLDOWN) return true;
        return false;
    }

    public void resetClassificationCooldown(){
        last_classification = System.currentTimeMillis();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CountDownLatch getSignalStop() {
        return signalStop;
    }

    public void resetStopSignal(){
        signalStop = new CountDownLatch(1);
    }

    public void triggerStopSignal(){
        if(signalStop != null) signalStop.countDown();
    }

    public void exportData(){
        if(state == State.CONNECTED){
            setState(State.EXPORTING);
        }
    }

    public List<Sensor> getUiSensors() {
        return sensors;
    }

    public float[] getCurrent_actual() {
        return current_actual;
    }

    public void setCurrent_actual(float[] current_actual) {
        this.current_actual = current_actual;
    }

    public ConfusionMatrix getCm_Grtl() {
        return cm_Grtl;
    }

    public ConfusionMatrix getCm_Handgelenk() {
        return cm_Handgelenk;
    }

    public ConfusionMatrix getCm_GrtlHandgelenk() {
        return cm_GrtlHandgelenk;
    }
}
