package de.carloschmitt.morec.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.ConfusionMatrix;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.runners.ExportRunner;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.repository.runners.ConnectionRunner;

public class MoRecRepository {
    private static final String TAG = "MoRecRepository";
    private static MoRecRepository instance;

    private MutableLiveData<State> state;
    private Context context;
    private String sessionName;

    //Connection
    private MutableLiveData<List<Sensor>> sensors;
    private CountDownLatch disconnectSignal;

    //Recording
    private MutableLiveData<List<Label>> labels;
    private int currentRecordingLabel;

    //Classification
    String[] modelLabels = {"Gehen", "Stehen", "Stolpern"};
    String[] requiredSensors = {"Guertel", "Handgelenk"};

    private long last_classification;
    private MutableLiveData<String> classificationResult;

    //EVALUATION TOOLS
    int[] evaluation_label_counter;
    private float[] current_actual;
    ConfusionMatrix cm_Grtl;
    ConfusionMatrix cm_Handgelenk;
    ConfusionMatrix cm_GrtlHandgelenk;
    private MutableLiveData<String> classificationEvaluation;
    HashMap<String, List<Long>> runtime_log;

    //Export
    private MutableLiveData<String> exportProgress;


    public MoRecRepository(){
        state = new MutableLiveData<>(State.INACTIVE);
        labels = new MutableLiveData<>(new ArrayList<Label>());
        sensors = new MutableLiveData<>(new ArrayList<>());

        disconnectSignal = null;

        currentRecordingLabel = -1;

        classificationResult = new MutableLiveData<>("Noch kein Ergebnis");
        last_classification = System.currentTimeMillis();

        exportProgress = new MutableLiveData<>("Exportiere ( 0% )");

        evaluation_label_counter = new int[3];
        current_actual = new float[] {0,0,0};
        cm_Grtl = new ConfusionMatrix(3, "Guertel_realtime");
        cm_Handgelenk = new ConfusionMatrix(3, "Handgelenk_realtime");
        cm_GrtlHandgelenk = new ConfusionMatrix(3, "GuertelHandgelenk_realtime");
        classificationEvaluation = new MutableLiveData<>("");
        runtime_log = new HashMap<>();
    }


    public void init(){
        sensors.getValue().add(new Sensor("Guertel", "00:0E:0E:16:8F:F6"));
        sensors.getValue().add(new Sensor("Handgelenk", "00:0E:0E:1B:60:DE"));

        labels.getValue().add(new Label("Gehen"));
        labels.getValue().add(new Label("Stehen"));
        labels.getValue().add(new Label("Stolpern"));
    }


    public static MoRecRepository getInstance() {
        if(instance == null){
            instance = new MoRecRepository();
            instance.init();
        }
        return instance;
    }

    /**
     * State Management
     */

    public MutableLiveData<State> getState() {
        return state;
    }

    public void setState(State state) {
        this.state.postValue(state);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * List Management
     */
    public MutableLiveData<List<Label>> getLabels() {
        return labels;
    }

    public MutableLiveData<List<Sensor>> getSensors() {
        return sensors;
    }

    public void addLabel(Label label){
        List<Label> old_Labels = labels.getValue();
        old_Labels.add(label);
        labels.postValue(old_Labels);
    }

    public void removeLabel(Label label){
        List<Label> old_Labels = labels.getValue();
        old_Labels.remove(label);
        labels.postValue(old_Labels);
    }
    public void addSensor(Sensor sensor){
        sensors.getValue().add(sensor);
        sensors.postValue(sensors.getValue());
    }

    public void removeSensor(Sensor sensor){
        sensors.getValue().remove(sensor);
        sensors.postValue(sensors.getValue());
    }



    /**
     * Connection Control
     */
    public void connectSensors(){
        if(state.getValue() != State.INACTIVE) return;

        disconnectSignal = new CountDownLatch(1);
        Thread connectionThread = new Thread(new ConnectionRunner(disconnectSignal));
        connectionThread.start();
    }

    public void triggerDisconnectSignal(){
        if(disconnectSignal != null) disconnectSignal.countDown();
    }


    /**
     * Recording Control
     */
    public void startRecording(int label_id){
        if(state.getValue() == State.CONNECTED){
            currentRecordingLabel = label_id;
            setState(State.RECORDING);
        }
        else Log.d(TAG, "Something went wrong... were not connected...");
    }

    public void stopRecording(){
        if(state.getValue() == State.RECORDING){
            setState(State.CONNECTED);
            currentRecordingLabel = -1;
        }
        else Log.d(TAG, "Something went wrong... We weren't recording...");
    }

    public int getCurrentRecordingLabel() {
        return currentRecordingLabel;
    }

    /**
     * Classification Control
     *
     */
    public void startClassifying(){
        if(state.getValue() == State.CONNECTED && requiredSensorsActive()) {
            setState(State.CLASSIFYING);
        }
    }
    public void stopClassifying(){
        if(state.getValue() == State.CLASSIFYING) {
            setState(State.CONNECTED);
            classificationResult.setValue("Klassifizierung gestoppt");
        }
    }

    public void setClassificationResult(String result){
        classificationResult.postValue(result);
    }
    public MutableLiveData<String> getClassificationResult() {
        return classificationResult;
    }

    public boolean checkClassificationCooldown(){
        long cooldown = System.currentTimeMillis() - last_classification;
        if(cooldown > Constants.CLASSIFICATION_COOLDOWN) return true;
        return false;
    }

    public void resetClassificationCooldown(){
        last_classification = System.currentTimeMillis();
    }

    public String[] getModelLabels(){
        return modelLabels;
    }

    private boolean requiredSensorsActive(){
        boolean ret = true;
        try{
            Log.d(TAG, "Checking if required sensors are there....");
            if(requiredSensors.length != sensors.getValue().size()) {
                Log.d(TAG, "Länge passt");
                ret = false;
            }
            for(int i = 0; i < requiredSensors.length; i++) {
                if(!requiredSensors[i].equals(sensors.getValue().get(i).getLive_name().getValue())){
                    ret = false;
                    Log.d(TAG, i + " passt nicht. "+ sensors.getValue().get(i).getLive_name().getValue());
                }
            }
        }catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
            ret = false;
        }
        return ret;
    }

    /**
     * Export control
     */
    public void exportData(){
        if(state.getValue() == State.INACTIVE){
            ExportRunner exportRunner = new ExportRunner();
            Thread exportThread = new Thread(exportRunner);
            exportThread.start();
        }
    }

    public void setExportProgress(String progress){
        exportProgress.postValue(progress);
    }

    public MutableLiveData<String> getExportProgress() {
        return exportProgress;
    }


    /**
     * Stuff for Evaluation Experiments
     *
     */
    public float[] getCurrent_actual() {
        return current_actual;
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

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionName(){
        return sessionName;
    }

    public HashMap<String, List<Long>> getRuntime_log() {
        return runtime_log;
    }

    public MutableLiveData<String> getClassificationEvaluation() {
        return classificationEvaluation;
    }

    public void setClassificationEvaluation(String classificationEvaluation) {
        this.classificationEvaluation.postValue(classificationEvaluation);
    }
}
