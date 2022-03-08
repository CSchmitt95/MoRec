package de.carloschmitt.morec.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.databinding.PageClassificationBinding;
import de.carloschmitt.morec.model.classification.ClassificationBuffer;
import de.carloschmitt.morec.model.classification.ClassificationRunner;
import de.carloschmitt.morec.ml.GrtelHandgelenk;
import de.carloschmitt.morec.ApplicationController;
import de.carloschmitt.morec.repository.util.State;
import de.carloschmitt.morec.viewmodel.ClassificationPageViewModel;


public class ClassificationPage extends Fragment {
    Context context;
    PageClassificationBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PageClassificationBinding.inflate(inflater, container, false);
        ClassificationPageViewModel classificationPageViewModel = new ViewModelProvider(getActivity()).get(ClassificationPageViewModel.class);
        binding.setClassificationPageViewModel(classificationPageViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        if(ApplicationController.state != State.CONNECTED) binding.btnStartstop.setEnabled(false);

        context = binding.getRoot().getContext();
        return binding.getRoot();
    }

    private class classificationRunner implements Runnable{
        private static final String TAG = "ClassificationRunner";

        List<float[]> records = new LinkedList<>();

        @Override
        public void run() {
            try {
                //List<String> labels = Files.readAllLines("");
                GrtelHandgelenk model = GrtelHandgelenk.newInstance(context);
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, ApplicationController.FLOATS_PER_WINDOW* ApplicationController.sensors.size()}, DataType.FLOAT32);

                Log.d(TAG, "Tariere Sensoren...");

                Log.d(TAG, "Done!");

                ClassificationBuffer classificationBuffer = new ClassificationBuffer();
                ClassificationRunner classificationRunner = new ClassificationRunner(classificationBuffer);

                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(classificationRunner,0,1000/ ApplicationController.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);

                Thread.sleep(5000);

                while(ApplicationController.state == State.CLASSIFYING){
                    Log.d(TAG, "Versuche zu klassifizieren...");
                    // Creates inputs for reference.

                    if(classificationBuffer.isSaturated()){
                        Log.d(TAG,"Classification Buffer is ready...");
                        //inputFeature0.loadArray(classificationBuffer.normalizedBufferFor(Data.sensors.get(0)));
                        float[] input = classificationBuffer.getBuffer();
                        records.add(input);
                        Log.d(TAG, "Records size: " + records.size());
                        inputFeature0.loadArray(input);
                        GrtelHandgelenk.Outputs outputs = model.process(inputFeature0);
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
            Log.d(TAG, "Classification beendet.");
            try
            {
                String foldername = new SimpleDateFormat("yyyyMMdd_HH:mm").format(new Date());
                File root = new File(context.getExternalFilesDir(null).toString(), foldername);
                if (!root.exists()) {
                    root.mkdirs();
                }

                File gpxfile = new File(root, "classification_recordings" + ".csv");
                FileWriter writer = new FileWriter(gpxfile);
                for(float[] recording : records){
                    writer.append(Float.toString(recording[0]));
                    for(int i = 1; i < recording.length; i++){
                        writer.append("," + recording[i]);
                    }
                    writer.append("\n");
                }
                writer.flush();
                writer.close();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
