package de.carloschmitt.morec.repository.runners;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.carloschmitt.morec.ml.Grtel;
import de.carloschmitt.morec.ml.GrtelHandgelenk;
import de.carloschmitt.morec.ml.Handgelenk;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Sensor;
import de.carloschmitt.morec.repository.util.ClassificationUtil;

public class ClassificationRunner implements Runnable{
    private final String TAG = "ClassificationRunner";
    private List<List<Quaternion>> input;
    private MoRecRepository moRecRepository;

    public ClassificationRunner(List<List<Quaternion>> input){
        this.moRecRepository = MoRecRepository.getInstance();
        this.input = input;
    }

    @Override
    public void run() {
        long beforeClassification = System.currentTimeMillis();
        try {
            List<Quaternion> processed_input = new ArrayList<>();
            for(List<Quaternion> raw_quaternions : input){
                List<Quaternion> diffQuaternions = ClassificationUtil.rawQuaternionsToDiffQuaternions(raw_quaternions);
                List<Quaternion> nullQuaternions = ClassificationUtil.nullifyQuaternions(diffQuaternions);
                processed_input.addAll(nullQuaternions);
            }

            Log.d(TAG, "Klassifiziere...");
            String[] labels = {"Stolpern", "Gehen", "Stehen"};

            float[] grtlHandgelenk_input = ClassificationUtil.allQuaternionsToFloat(processed_input);
            float[] grtl_input = ClassificationUtil.sensorQuaternionsToFloat(0,processed_input);
            float[] handgelenk_input = ClassificationUtil.sensorQuaternionsToFloat(1, processed_input);

            Log.d(TAG, "float input size : " + grtlHandgelenk_input.length);
            TensorBuffer inputFeatureGrtlHandgelenk = TensorBuffer.createFixedSize(new int[]{1, 3000 }, DataType.FLOAT32);
            TensorBuffer inputFeatureGrtl = TensorBuffer.createFixedSize(new int[]{1, 1500 }, DataType.FLOAT32);
            TensorBuffer inputFeatureHandgelenk = TensorBuffer.createFixedSize(new int[]{1, 1500 }, DataType.FLOAT32);

            inputFeatureGrtlHandgelenk.loadArray(grtlHandgelenk_input);
            inputFeatureGrtl.loadArray(grtl_input);
            inputFeatureHandgelenk.loadArray(handgelenk_input);

            GrtelHandgelenk grtelHandgelenk = GrtelHandgelenk.newInstance(moRecRepository.getContext());
            Grtel grtl = Grtel.newInstance(moRecRepository.getContext());
            Handgelenk handgelenk = Handgelenk.newInstance(moRecRepository.getContext());

            GrtelHandgelenk.Outputs grtlHandgelenk_outputs = grtelHandgelenk.process(inputFeatureGrtlHandgelenk);
            Grtel.Outputs grtl_outputs = grtl.process(inputFeatureGrtl);
            Handgelenk.Outputs handgelenk_outputs = handgelenk.process(inputFeatureHandgelenk);

            TensorBuffer outputFeature_grtlHandgelenk = grtlHandgelenk_outputs.getOutputFeature0AsTensorBuffer();
            TensorBuffer outputFeature_Grtl = grtl_outputs.getOutputFeature0AsTensorBuffer();
            TensorBuffer outputFeature_Handgelenk = handgelenk_outputs.getOutputFeature0AsTensorBuffer();

            float[] result_GrtlHandgelenk = outputFeature_grtlHandgelenk.getFloatArray();
            float[] result_Grtl = outputFeature_Grtl.getFloatArray();
            float[] result_Handgelenk = outputFeature_Handgelenk.getFloatArray();

            moRecRepository.getCm_GrtlHandgelenk().addValue(result_GrtlHandgelenk,moRecRepository.getCurrent_actual());
            moRecRepository.getCm_Grtl().addValue(result_Grtl, moRecRepository.getCurrent_actual());
            moRecRepository.getCm_Handgelenk().addValue(result_Handgelenk, moRecRepository.getCurrent_actual());

            String[] results = new String[3];
            results[0] = ClassificationUtil.getMostProbableLabel(labels, result_GrtlHandgelenk);
            results[1] = ClassificationUtil.getMostProbableLabel(labels, result_Grtl);
            results[2] = ClassificationUtil.getMostProbableLabel(labels, result_Handgelenk);

            moRecRepository.setClassificationResult("Kombo:\n" + moRecRepository.getCm_GrtlHandgelenk().toString() +
                    "\n\nGürtel:\n" + moRecRepository.getCm_Grtl().toString() +
                    "\n\nHandgelenk:\n" + moRecRepository.getCm_Handgelenk().toString() +
                    "\n\nCounter:\n " + ClassificationUtil.getValuesWithLabels(moRecRepository.getCm_GrtlHandgelenk().getSumsOfActuals(), labels) +
                    "\n\nKonflikte:\n" + ClassificationUtil.getConflictString(results));


            /*moRecRepository.setClassificationResult("Kombo:\n" + ClassificationUtil.getResultString(labels,result_GrtlHandgelenk) +
                    "\n\nGürtel:\n" + ClassificationUtil.getResultString(labels,result_Grtl) +
                    "\n\nHandgelenk:\n" + ClassificationUtil.getResultString(labels, result_Handgelenk)+
                    "\n\nKonflikte:\n" + ClassificationUtil.getConflictString(results));
            */
        } catch (IOException e) {

            e.printStackTrace();
            moRecRepository.setClassificationResult(e.getMessage());
        }
        long afterClassification = System.currentTimeMillis();
        Log.d(TAG, "Klassifizeriung abgeschlossen. ( " + (afterClassification-beforeClassification) + "ms )");
        List<Long> log = moRecRepository.getRuntime_log().get("ClassificationRunner");
        if(log == null) log = new ArrayList<>();
        log.add(afterClassification-beforeClassification);
        moRecRepository.getRuntime_log().put("ClassificationRunner",log);
    }
}
