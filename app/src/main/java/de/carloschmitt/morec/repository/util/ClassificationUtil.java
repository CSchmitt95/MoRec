package de.carloschmitt.morec.repository.util;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.ArrayList;
import java.util.List;

import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.MoRecRepository;

public class ClassificationUtil {

    /**
     * Gibt eine Liste von Quaternionen als Arrayliste von floats zurück.
     * @param quaternionList Eingangsliste
     * @return floatarray mit den werten aus der Eingangsliste.
     */
    public static float[] allQuaternionsToFloat(List<Quaternion> quaternionList){
        float[] ret = new float[quaternionList.size() * 4];

        for(int i = 0; i < quaternionList.size(); i++){
            ret[i*4 + 0] = quaternionList.get(i).getW();
            ret[i*4 + 1] = quaternionList.get(i).getX();
            ret[i*4 + 2] = quaternionList.get(i).getY();
            ret[i*4 + 3] = quaternionList.get(i).getZ();
        }

        return ret;
    }

    /**
     * Gibt aus einer Quaternionenliste mit mehreren Sensoren die Float-Komponenten der Quaternionen eines Sensors zurück.
     * @param sensor die Daten welches Sensors sollen zurückgegeben werden?
     * @param quaternionList Aus welcher Liste sollen die Daten genommen werden?
     * @return Ein Float-Array, mit den Komponenten der angegebenen Quaternionen
     */
    public static float[] sensorQuaternionsToFloat(int sensor, List<Quaternion> quaternionList){
        float[] ret = new float[Constants.QUATERNIONS_PER_WINDOW * 4];

        for(int i = 0; i < Constants.QUATERNIONS_PER_WINDOW; i++){
            ret[i*4 + 0] = quaternionList.get(i+Constants.QUATERNIONS_PER_WINDOW*sensor).getW();
            ret[i*4 + 1] = quaternionList.get(i+Constants.QUATERNIONS_PER_WINDOW*sensor).getX();
            ret[i*4 + 2] = quaternionList.get(i+Constants.QUATERNIONS_PER_WINDOW*sensor).getY();
            ret[i*4 + 3] = quaternionList.get(i+Constants.QUATERNIONS_PER_WINDOW*sensor).getZ();
        }

        return ret;
    }


    /**
     * Gibt für eine Datenreihe an Quaternionen die entsprechenden Differenzquaternionen für jeden Schritt zurück.
     * Die Größe der liste verringert sich entsprechend um 1!
     * @param quaternionList Liste an Quaternionen die als Quelle benutzt werden.
     * @return eine Liste an Differenzquaternionen.
     */
    public static List<Quaternion> rawQuaternionsToDiffQuaternions(List<Quaternion> quaternionList){
        List<Quaternion> ret = new ArrayList<>();
        for (int i = 0; i < quaternionList.size() - 1; i++) {
            Quaternion current = quaternionList.get(i);
            Quaternion next = quaternionList.get(i + 1);

            Quaternion diff = next.mult(current.inverse());
            ret.add(diff);
        }
        return ret;
    }

    /**
     * Nulll die Komponenten der Quaternionen an der ersten Quaternion der Liste.
     * @param quaternionList Die Liste der Quaternionen, die genullt werden sollen.
     * @return die genullte Liste.
     */
    public static List<Quaternion> nullifyQuaternions(List<Quaternion> quaternionList){
        Quaternion first = quaternionList.get(0);

        List<Quaternion> ret = new ArrayList<>();
        ret.add(new Quaternion(0,0,0,0));

        for(int i = 1; i < quaternionList.size(); i++){
            Quaternion current = quaternionList.get(i);
            ret.add(new Quaternion(
                    current.getW() - first.getW(),
                    current.getX() - first.getX(),
                    current.getY() - first.getY(),
                    current.getZ() - first.getZ()));
        }
        return ret;
    }

    /**
     * Diese Methode findet raus, ob die beiden Modelle der einzel-Sensoren unterschiedliche Ergebnisse liefern.
     * @param results
     * @return
     */
    public static String getConflictString(String[] results){
        String ret = "";
        String[] sensor_names = new String[MoRecRepository.getInstance().getSensors().getValue().size() + 1];
        sensor_names[0] = "Kombo";
        for(int i = 1; i < sensor_names.length; i++) {
            sensor_names[i] = MoRecRepository.getInstance().getSensors().getValue().get(i-1).getLive_name().getValue();
        }
        for(int i = 1; i < results.length; i++){
            for(int j = i; j < results.length; j++){
                if(!results[i].equals(results[j])) ret += sensor_names[i] +" sagt " + results[i] + ", aber "+ sensor_names[j] +" sagt " + results[j] + "\n"+
                        "Kombo übernimmt: " + (results[0].equals(results[i]) ? sensor_names[i] : sensor_names[j] + "\n");
            }
        }
        return ret;
    }

    /**
     * GIbt das Label mit der höchsten Wahrscheinlichkeit zurück.
     * @param labels
     * @param result
     * @return
     */
    public static String getMostProbableLabel(String[] labels, float[] result){
        int label_index = indexOfMax(result);
        return labels[label_index];
    }

    public static String getResultString(String[] labels, float[] result){
        int label_index = indexOfMax(result);
        float prob = result[label_index];

        String ret = "";// label + "(" + format_prob + ")";
        for(int i = 0; i < labels.length; i++){
            ret += labels[i] + " (" + String.format("%.02f", result[i]) + ") \n";
        }
        return ret;
    }

    public static int indexOfMax(float[] array){
        int max = 0;
        int index = 0;
        while (index < array.length){
            if (array[index] > array[max]) max = index;
            index++;
        }
        return max;
    }

    public static String matrixToString(int[][] matrix) {
        String ret = "";
        for(int i = 0; i < matrix.length; i ++){
            for(int j = 0 ; j < matrix[i].length; j++){
                ret += " [" + matrix[i][j] + "] ";
            }
            ret+= "\n";
        }
        return ret;
    }

    public static String getValuesWithLabels(int[] values, String[] labels){
        String ret = "";
        if(values.length == labels.length){
            for(int i = 0; i < values.length; i++){
                ret += labels[i] + ": " + values[i] + " ";
            }
        }
        return ret;
    }
}
