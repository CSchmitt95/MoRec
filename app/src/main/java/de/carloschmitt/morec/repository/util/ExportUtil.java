package de.carloschmitt.morec.repository.util;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Label;
import de.carloschmitt.morec.repository.model.Sample;

public class ExportUtil {

    /**
     * Findet den index ab dem eine Bewegung gelabelt wurde.
     * Wenn kein Label mehr gefunden wird, wird -1 zurückgegeben.
     * @param current_index der Index ab dem Gesucht werden soll. Der aktuelle Index wird eingeschlossen.
     * @param samples die Sample List die Dursucht werden soll.
     * @return Index des nächsten Labels, wenn nicht vorhanden -1
     */
    public static int findIndexOfNextMovementBeginning(int current_index, List<Sample> samples){
        int ret = -1;
        for(int i = current_index; i < samples.size(); i++){
            if (samples.get(i).getLabel_id() != -1) return i;
        }
        return -1;
    }

    /**
     * Gibt die Länge der Bewegung zurück in der sich der Index gerade Befindet.
     * @param current_index
     * @param samples
     * @return
     */
    public static int getLengthOfCurrentLabel(int current_index, List<Sample> samples){
        int current_label = samples.get(current_index).getLabel_id();
        int ret = 1;
        for(int i = current_index + 1 ; i < samples.size(); i++){
            if(samples.get(i).getLabel_id() == current_label) ret++;
            else break;
        }
        return ret;
    }


    public static int findNextStartOf(Label label, int current_index, List<Sample> samples){
        int ret = -1;
        for(int i = current_index; i < samples.size(); i++){
            if (samples.get(i).getLabel_id() == label.getLabel_id().getValue()) return i;
        }
        return -1;
    }

    public static String getQuaternionStringFromTo(int start, int stop, List<Sample> samples){
        String ret = "";
        for(int i = start; i < stop; i++){
            Quaternion q = samples.get(i).getQuaternion();
            ret += "," + q.getW() + "," + q.getX() + "," + q.getY() + "," + q.getZ();
        }
        return ret;
    }

    public static int findStartOfNextLabel(int current_index, List<Sample> samples){
        for(int i = current_index; i < samples.size(); i++){
            if (samples.get(i).getLabel_id() != -1) return i;
        }
        return samples.size();
    }

    public static Label findLabelWithId(int id){
        Label ret = null;
        for(Label label : MoRecRepository.getInstance().getLabels().getValue()){
            if(label.getLabel_id().getValue() == id) ret = label;
        }
        return ret;
    }

    public static void writeQuaternionsToFile(String label_text, String sensor_name, int record_id, int extract_start, int extract_end, List<Sample> samples, FileWriter writer) throws IOException {
        writer.append(label_text + "," + sensor_name + ","+ record_id);
        for(int i = extract_start; i < extract_end; i++){
            Quaternion q = samples.get(i).getQuaternion();
            writer.append("," + q.getW() + "," + q.getX() + "," + q.getY() + "," + q.getZ());
        }
        writer.append("\n");
    }

}
