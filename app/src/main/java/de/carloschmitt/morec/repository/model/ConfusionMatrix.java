package de.carloschmitt.morec.repository.model;

import android.util.Log;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.util.ClassificationUtil;
import de.carloschmitt.morec.repository.Constants;

/**
 * Konfusionsmatrix-Klasse.
 * Kann verwendet werden um die Performanz eines Netzes in echtzeit zu evaluieren.
 * addValue wird verwendet um Werte einzutragen.
 * to String gibt die Matrix als Text aus
 *
 */
public class ConfusionMatrix {
    private static final String TAG = "ConfusionMatrix";
    private int[][] matrix;
    private String name;

    public ConfusionMatrix(int size, String name){
        matrix = new int[size][size+1];
        this.name = name;
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

    @Override
    public String toString(){
        String ret = "\t\t\t\t\t\t";
        for(String label : MoRecRepository.getInstance().getModelLabels()){
            ret += label + " ";
        }
        ret +="\n";

        for(int i = 0; i < matrix.length; i ++){
            ret += MoRecRepository.getInstance().getModelLabels()[i] + "\t";
            for(int j = 0 ; j < matrix[i].length; j++){
                ret += " [" + matrix[i][j] + "] ";
            }
            ret+= "\n";
        }
        return ret;
    }

    public int[] getSumsOfActuals(){
        int[] ret = new int[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                ret[i] += matrix[i][j];
            }
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public String getLine(int line){
        if(line >= matrix.length) return null;
        String ret = "";
        for (int i = 0; i < matrix[line].length; i++) ret += ","+ Integer.toString(matrix[line][i]);
        return ret;
    }
}
