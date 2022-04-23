package de.carloschmitt.morec.repository.model;

import android.util.Log;

import de.carloschmitt.morec.repository.util.ClassificationUtil;
import de.carloschmitt.morec.repository.util.Constants;

public class ConfusionMatrix {
    private static final String TAG = "ConfusionMatrix";
    private int[][] matrix;

    public ConfusionMatrix(int size){
        matrix = new int[size][size+1];
    }

    public void addValue(float[] predicted, float[] actual){
        int index_predicted = ClassificationUtil.indexOfMax(predicted);
        int index_actual = ClassificationUtil.indexOfMax(actual);

        Log.d(TAG, "index Predicted: " + index_predicted + " index actual: " + index_actual);
        //Check ob es ein actual gibt.
        if(actual[index_actual] < 0.5) return;
        //Wenn die Konfidenz zu gering -> in die not sure Klasse.
        if(predicted[index_predicted] < Constants.CONFIDENCE_THRESHHOLD) index_predicted = matrix[index_actual].length-1;
        Log.d(TAG, "index of actual is: " + actual[index_actual] );
        matrix[index_actual][index_predicted]++;
    }

    public int[][] getMatrix(){
        return matrix;
    }

    public float getSensitivityOf(int index){
        int sum = 0;
        for(int i = 0; i < matrix.length; i++){
            sum += matrix[index][i];
        }
        return matrix[index][index]/sum;
    }

    public float getSpecificityOf(int index){
        int sum = 0;
        for(int i = 0; i < matrix.length; i++){
            sum += matrix[i][index];
        }
        return matrix[index][index]/sum;
    }

    @Override
    public String toString(){
        String ret = "";
        for(int i = 0; i < matrix.length; i ++){
            for(int j = 0 ; j < matrix[i].length; j++){
                ret += " [" + matrix[i][j] + "] ";
            }
            ret+= "\n";
        }
        return ret;
    }

    public int[] getSumsOfActuals(){
        int[] ret = new int[matrix.length];
        for(int i = 0; i < matrix.length; i++){,
            for(int j = 0; j < matrix[i].length; j++){
                ret[i] += matrix[i][j];
            }
        }
        return ret;
    }
}
