package de.carloschmitt.morec.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.ProgressBar;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.carloschmitt.morec.adapters.MovementItemAdapter;
import de.carloschmitt.morec.adapters.SensorItemAdapter;
import de.carloschmitt.morec.recording.Recorder;
import de.carloschmitt.morec.recording.Recording;
import de.carloschmitt.morec.viewmodels.ClassificationViewModel;

public class Data {
    //Wichtige Konstanten
    private static Data instance;
    public static final String TAG = "Data";
    public static final int WINDOW_SIZE_IN_S = 3;
    public static final double OVERLAP_IN_S = 0.25;
    public static final int SAMPLES_PER_SECOND = 125;
    public static final int SLEEP_TIME = 1000/SAMPLES_PER_SECOND;
    public static final int OVERLAP = (int) Math.ceil(OVERLAP_IN_S * SAMPLES_PER_SECOND);
    public static final int MAX_SAMPLES = WINDOW_SIZE_IN_S * SAMPLES_PER_SECOND + 2 * OVERLAP ;
    public static final int QUATERNIONS_PER_WINDOW = SAMPLES_PER_SECOND * WINDOW_SIZE_IN_S;
    public static final int FLOATS_PER_WINDOW = QUATERNIONS_PER_WINDOW * 4;
    public static Context applicationContext;

    public enum State{
        INACTIVE,
        CONNECTING,
        CONNECTED,
        RECORDING,
        CLASSIFYING,
        EXPORTING
    }

    public static State state;
    // Listen bezüglich aller Sensoren und Bewegungen
    public static List<Movement> movements;
    public static List<Sensor> sensors;
    public static Recorder recorder;

    //Wichtige Zustände:
    public static MovementItemAdapter movementItemAdapter;
    public static SensorItemAdapter sensorItemAdapter;

    //Testing
    private static ClassificationViewModel classificationViewModel;


    private Data(){
        movements = new ArrayList<>();
        sensors = new ArrayList<>();
        recorder = null;

        sensors.add(new Sensor("Gürtel", "00:0E:0E:16:8F:F6")); // UUID: 00001101-0000-1000-8000-00805f9b34fb
        sensors.add(new Sensor("Handgelenk","00:0E:0E:1B:60:DE")); // UUID: 00001101-0000-1000-8000-00805f9b34fb

        movements.add(new Movement("Gehen",false));
        movements.add(new Movement("Stehen",false));
        movements.add(new Movement("Stolpern",true));

        movementItemAdapter = null;
        sensorItemAdapter = null;
        state = State.INACTIVE;
    }

    public static Data getInstance(){
        if (instance == null) return new Data();
        else return instance;
    }

    public static void startRecording(Movement movement){
        if(state == State.CONNECTED){
            recorder.startRecording(movement);
        }
    }

    public static void stopRecording(){
        if(state == State.RECORDING){
            recorder.stopRecording();
        }
    }

    public static void connectSensors(){
        if(state == State.INACTIVE) {
            Log.d(TAG, "Verbinde alle Sensoren...");
            recorder = new Recorder();
            state = State.CONNECTING;
        }
    }

    public static void disconnectSensors(){
        if(state == State.CONNECTED){
            Log.d(TAG, "Trenne alle Sensoren...");
            if(recorder != null) {
                recorder.destroy();
                recorder = null;
            }
        }
    }

    public static void exportData(ProgressBar bar){
        Thread exporterThread = new Thread(new DataExporter(bar));
        exporterThread.start();
    }

    private static class DataExporter implements Runnable{
        String TAG = "DateExporter";
        ProgressBar bar;

        public DataExporter(ProgressBar bar){
            this.bar = bar;
        }

        @Override
        public void run() {
            try
            {
                String foldername = new SimpleDateFormat("yyyyMMdd_HH:mm").format(new Date());
                File root = new File(applicationContext.getExternalFilesDir(null).toString(), foldername);
                if (!root.exists()) {
                    root.mkdirs();
                }
                bar.setMax(movements.size()*100);
                bar.setIndeterminate(false);
                for(Movement movement : Data.movements){
                    bar.setProgress(movements.indexOf(movement)*100);
                    File gpxfile = new File(root, movement.name + ".csv");
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append("MovementName,SensorName,Record_id,");
                    writer.append("x0,y0 z0,w0... wn, xn, yn, zn\n");
                    for(Recording recording : movement.getRecordings()){
                        bar.setProgress(movements.indexOf(movement) * 100+ ( movement.getRecordings().indexOf(recording) / movement.getRecordings().size())*99 );
                        writer.append(recording.getMovement().getName() + "," + recording.getSensor().getName() + "," + recording.getSession_id() + recording.getQuaternionsAsString() + "\n");
                    }
                    writer.flush();
                    writer.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    bar.setVisibility(View.GONE);
                }
            }, 100);
        }
    }

    private static void changeStateTo(Data.State new_state){
        state = new_state;
        classificationViewModel.notifyChange();
    }


    public static void testShit(){
        /*
                -0.6667164,-0.13293989,0.35670593,-0.64076304,
                -0.66609234,-0.13077115,0.35456434,-0.6430428,
         */
        float[] test = {-0.6667164f,-0.13293989f, 0.35670593f,-0.64076304f, -0.66609234f,-0.13077115f,0.35456434f,-0.6430428f};



        Quaternion q1 = new Quaternion(test[0],test[1], test[2],test[3]);
        Quaternion q2 = new Quaternion(test[4],test[5], test[6],test[7]);

        Quaternion inverse = q1.inverse();

        Log.d(TAG, "INVERSE_32: " + inverse.toString());
        Log.d(TAG, "INVERSE: " + Integer.toBinaryString(Float.floatToIntBits(inverse.getW())) + " +" + Integer.toBinaryString(Float.floatToIntBits(inverse.getX())) + "i +" + Integer.toBinaryString(Float.floatToIntBits(inverse.getY())) + "j " + Integer.toBinaryString(Float.floatToIntBits(inverse.getZ())) + "k");
        Quaternion diff = q2.mult(inverse);

        Log.d(TAG, "DIFF_32 : " + Integer.toBinaryString(Float.floatToIntBits(diff.getW())) + " +" + Integer.toBinaryString(Float.floatToIntBits(diff.getX())) + "i +" + Integer.toBinaryString(Float.floatToIntBits(diff.getY())) + "j " + Integer.toBinaryString(Float.floatToIntBits(diff.getZ())) + "k");


        double w = q1.getW()*q1.getW() + q1.getX()*q1.getX() + q1.getY()*q1.getY() + q1.getZ()*q1.getZ();

        double[] q1_inv = { q1.getW()/w, -q1.getX()/w , -q1.getY()/w, -q1.getZ()/w };
        Log.d(TAG, "INVERSE_64 :" + q1_inv[0] + " + " + q1_inv[1] + "i +" + q1_inv[2] + "j + " + q1_inv[3] + "k");
        Log.d(TAG, "INVERSE_64 : " + Long.toBinaryString(Double.doubleToLongBits(q1_inv[0])) + " +" + Long.toBinaryString(Double.doubleToLongBits(q1_inv[1])) + "i +" + Long.toBinaryString(Double.doubleToLongBits(q1_inv[2])) + "j " + Long.toBinaryString(Double.doubleToLongBits(q1_inv[3])) + "k");

        double[] diff_64 = {
                q2.getW() * q1_inv[0] - q2.getX() * q1_inv[1] - q2.getY() * q1_inv[2] - q2.getZ() * q1_inv[3],
                q2.getW() * q1_inv[1] + q2.getX() * q1_inv[0] + q2.getY() * q1_inv[3] - q2.getZ() * q1_inv[2],
                q2.getW() * q1_inv[2] - q2.getX() * q1_inv[3] + q2.getY() * q1_inv[0] + q2.getZ() * q1_inv[1],
                q2.getW() * q1_inv[3] + q2.getX() * q1_inv[2] - q2.getY() * q1_inv[1] + q2.getZ() * q1_inv[0]
        };

        Log.d(TAG, "DIFF_64 :" + diff_64[0] + " + " + diff_64[1] + "i +" + diff_64[2] + "j + " + diff_64[3] + "k");
        Log.d(TAG, "DIFF_64 : " + Long.toBinaryString(Double.doubleToLongBits(diff_64[0])) + " +" + Long.toBinaryString(Double.doubleToLongBits(diff_64[1])) + "i +" + Long.toBinaryString(Double.doubleToLongBits(diff_64[2])) + "j " + Long.toBinaryString(Double.doubleToLongBits(diff_64[3])) + "k");


        Quaternion q_diff64 = new Quaternion( (float)diff_64[0],  (float)diff_64[1], (float)diff_64[2], (float) diff_64[3]);

        Log.d(TAG, "q_diff64: " + q_diff64.toString());
        Log.d(TAG, "q_diff32: " + diff.toString());
        //Inverse
        /*
        float d = w*w + x*x + y*y + z*z;
        return new Quaternion( w/d, -x/d, -y/d, -z/d );
         */


        //Multiplikation
        /*
        float new_w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        float new_x = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        float new_y = this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x;
        float new_z = this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w;
         */
    }
}
