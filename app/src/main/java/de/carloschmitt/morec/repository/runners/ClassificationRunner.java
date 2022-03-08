package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.List;

import de.carloschmitt.morec.ml.GrtelHandgelenk;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.util.ClassificationUtil;

public class ClassificationRunner implements Runnable{
    private final String TAG = "ClassificationRunner";
    private List<Quaternion> input;
    private MoRecRepository moRecRepository;

    public ClassificationRunner(List<Quaternion> input){
        this.moRecRepository = MoRecRepository.getInstance();
        this.input = input;
    }

    @Override
    public void run() {
        long beforeClassification = System.currentTimeMillis();
        try {
            Log.d(TAG, "Klassifiziere...");
            String[] labels = {"Stolpern", "Gehen", "Stehen"};
            float[] float_input = ClassificationUtil.quaternionsToFloat(input);
            Log.d(TAG, "float input size : " + float_input.length);
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 3000 }, DataType.FLOAT32);
            inputFeature0.loadArray(float_input);
            GrtelHandgelenk model = GrtelHandgelenk.newInstance(moRecRepository.getContext());
            GrtelHandgelenk.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] tensor_result = outputFeature0.getFloatArray();
            moRecRepository.setClassificationResult(ClassificationUtil.getResultString(labels,tensor_result));
        } catch (IOException e) {
            e.printStackTrace();
            moRecRepository.setClassificationResult(e.getMessage());
        }
        long afterClassification = System.currentTimeMillis();
        Log.d(TAG, "Klassifizeriung abgeschlossen. ( " + (afterClassification-beforeClassification) + "ms )");

    }
}
