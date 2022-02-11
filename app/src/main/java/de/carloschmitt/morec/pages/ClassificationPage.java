package de.carloschmitt.morec.pages;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigatorExtrasKt;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.classification.ClassificationBuffer;
import de.carloschmitt.morec.classification.ClassificationRunner;
import de.carloschmitt.morec.databinding.FragmentPageClassificationBinding;
import de.carloschmitt.morec.ml.*;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Sensor;

import org.tensorflow.lite.support.common.FileUtil;



public class ClassificationPage extends Fragment {
    Context context;
    FragmentPageClassificationBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPageClassificationBinding.inflate(inflater, container, false);

        if(Data.state != Data.State.CONNECTED) binding.btnStartstop.setEnabled(false);

        for(Sensor sensor : Data.sensors){
            sensor.tare();
        }

        context = binding.getRoot().getContext();
        binding.btnStartstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.state == Data.State.CONNECTED){
                    Data.state = Data.State.CLASSIFYING;
                    binding.btnStartstop.setText("STOP");
                    Thread classificationThread = new Thread(new classificationRunner());
                    classificationThread.start();
                }
                else if(Data.state == Data.State.CLASSIFYING){
                    Data.state = Data.State.CONNECTED;
                    binding.btnStartstop.setText("START");
                }
            }
        });
        return binding.getRoot();
    }

    private class classificationRunner implements Runnable{
        private static final String TAG = "ClassificationRunner";

        @Override
        public void run() {
            try {
                Grtel model = Grtel.newInstance(context);
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 1500}, DataType.FLOAT32);

                ClassificationBuffer classificationBuffer = new ClassificationBuffer();
                ClassificationRunner classificationRunner = new ClassificationRunner(classificationBuffer);

                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(classificationRunner,0,1000/Data.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);

                Thread.sleep(5000);

                while(Data.state == Data.State.CLASSIFYING){
                    Log.d(TAG, "Versuche zu klassifizieren...");
                    // Creates inputs for reference.

                    if(classificationBuffer.isSaturated()){
                        Log.d(TAG,"Classification Buffer is ready...");
                        inputFeature0.loadArray(classificationBuffer.normalizedBufferFor(Data.sensors.get(0)));
                        Grtel.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                        Log.d(TAG, Arrays.toString(inputFeature0.getFloatArray()));
                        float tensor_result[] = outputFeature0.getFloatArray();
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.txtResult.setText("Stolpern: " + String.format("%.02f",tensor_result[0])
                                        + "\nGehen: " + String.format("%.02f",tensor_result[1])
                                        + "\nStehen: " + String.format("%.02f",tensor_result[2]));
                            }
                        },100);
                    }
                    // Runs model inference and gets result.
                    Thread.sleep(3000);
                }
                future.cancel(false);
                // Releases model resources if no longer used.
                model.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                // TODO Handle the exception
            }
        }
    }
}
