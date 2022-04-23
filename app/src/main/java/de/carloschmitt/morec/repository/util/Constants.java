package de.carloschmitt.morec.repository.util;

public class Constants {
    public static final String TAG = "Data";
    public static final int WINDOW_SIZE_IN_S = 3;
    public static final double OVERLAP_IN_S = 0.25;
    public static final int SAMPLES_PER_SECOND = 125;
    public static final int SLEEP_TIME = 1000/SAMPLES_PER_SECOND;
    public static final int OVERLAP = (int) Math.ceil(OVERLAP_IN_S * SAMPLES_PER_SECOND);
    public static final int MAX_SAMPLES = WINDOW_SIZE_IN_S * SAMPLES_PER_SECOND + 2 * OVERLAP ;
    public static final int QUATERNIONS_PER_WINDOW = SAMPLES_PER_SECOND * WINDOW_SIZE_IN_S;
    public static final int FLOATS_PER_WINDOW = QUATERNIONS_PER_WINDOW * 4;
    public static final float CLASSIFICATIONS_PER_SECOND = 1;
    public static final float CLASSIFICATION_EVERY_X_SECONDS = 1;
    public static final long CLASSIFICATION_COOLDOWN = (long)(1000*CLASSIFICATION_EVERY_X_SECONDS);
    public static final int SENSOR_HEALTH_THRESHHOLD = SAMPLES_PER_SECOND*2;
    public static final float CONFIDENCE_THRESHHOLD = 0.9f;
}
