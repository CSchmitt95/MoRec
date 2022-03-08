package de.carloschmitt.morec.repository.util;

import android.util.Log;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class ClassificationUtil {

    /**
     * Gibt eine Liste von Quaternionen als Arrayliste von floats zurück.
     * @param quaternionList Eingangsliste
     * @return floatarray mit den werten aus der Eingangsliste.
     */
    public static float[] quaternionsToFloat(List<Quaternion> quaternionList){
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


    public static String getResultString(String[] labels, float[] result){
        int label_index = indexOfMax(result);
        float prob = result[label_index];
        String label = labels[label_index];
        String format_prob = String.format("%.02f", prob);

        String ret = "Ergebnis:\n";// label + "(" + format_prob + ")";
        for(int i = 0; i < labels.length; i++){
            ret += labels[i] + " (" + String.format("%.02f", result[i]) + ") \n";
        }
        return ret;
    }

    private static int indexOfMax(float[] array){
        int max = 0;
        int index = 0;
        while (index < array.length){
            if (array[index] > array[max]) max = index;
            index++;
        }
        return max;
    }
}
