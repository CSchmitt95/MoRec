package de.carloschmitt.morec.repository.util;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.Random;

import de.carloschmitt.morec.repository.Constants;
import de.carloschmitt.morec.repository.MoRecRepository;
import de.carloschmitt.morec.repository.model.Sample;
import de.carloschmitt.morec.repository.model.Sensor;

public class EvalUtil {
    /**
     * Diese Methode füllt die RecordBuffer der Sensoren mit synthteischen daten.
     * Dafür werden iterativ Aufnahmen in die Buffer geschrieben.
     * @param session_lenght_in_s
     * @param max_record_length_in_s
     */
    public static void fill_buffers_with(int session_lenght_in_s, int max_record_length_in_s, int number_of_recordings){
        Random random = new Random();
        int record_length_s = max_record_length_in_s / number_of_recordings;
        int pause_length_s = (session_lenght_in_s-max_record_length_in_s-3) / number_of_recordings;

        //Leerlauf von 3 sekunden.
        for(Sensor sensor: MoRecRepository.getInstance().getSensors().getValue()) {
            for(int i = 0; i < 3 * Constants.SAMPLES_PER_SECOND; i++) {
                sensor.getRecordBuffer().add(new Sample(new Quaternion(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()), -1));
            }
        }

        for(int i = 0; i < number_of_recordings; i++){
            int random_label = random.nextInt(MoRecRepository.getInstance().getLabels().getValue().size());
            for(Sensor sensor: MoRecRepository.getInstance().getSensors().getValue()) {
                for (int j = 0; j < record_length_s * Constants.SAMPLES_PER_SECOND; j++) {
                    sensor.getRecordBuffer().add(new Sample(new Quaternion(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()), random_label));
                }
                for (int j = 0; j < pause_length_s * Constants.SAMPLES_PER_SECOND; j++) {
                    sensor.getRecordBuffer().add(new Sample(new Quaternion(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()), -1));
                }
            }
        }
    }
}
