package com.meicke.threeSpaceSensorAndroidAPI.Dtw;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.Arrays;
import java.util.List;

/**
 * Contained Dynamic Time Warping class. Restricts the cost matrix computation by utilizing a fixed
 * sized sliding window. Test results show a motion recognition capability of 96.92% and the
 * runtime is on average 1.83 times faster than regular DTW (all tests are based on a window size
 * of 25).
 */
public class CDTW extends AbstractDTW {

    /**
     * Size of the sliding window. This value is interpreted as the width of bigger time series.
     */
    private final int windowSizePercentage;

    /**
     * CDTW constructor with default window size.
     */
    public CDTW () {
        this.windowSizePercentage = 25;
    }

    /**
     * CDTW constructor with variable window size.
     * @param windowSizePercentage Window size.
     */
    public CDTW (int windowSizePercentage) {
        this.windowSizePercentage = windowSizePercentage;
    }

    /**
     * Computes the warp distance between the parameters s1 and s2.
     * @param s1 First quaternion time series.
     * @param s2 Second quaternion time series.
     * @return Warp distance between both parameters as float value.
     */
    public float getWarpDistance (List<Quaternion> s1, List<Quaternion> s2) {

        // Pad time series (padding the 0th row and column is easier than coding a multi condition
        // if statement to avoid out of bounds errors for the first row and column)
        Quaternion padding = new Quaternion(1,0,0,0);
        s1.add(0, padding);
        s2.add(0, padding);

        // Create cost matrix
        int sizeS1 = s1.size();
        int sizeS2 = s2.size();
        float [] [] costMatrix = new float [sizeS1] [sizeS2];

        // Prepare constraint window (some type casting is needed here,
        // to make sure to get a proper window size, even for small (<100) time series)
        int maxDimension = Math.max(sizeS1,sizeS2);
        float maxDimensionPercentile = (float) maxDimension / 100;
        int w = Math.round(Float.max(maxDimensionPercentile * windowSizePercentage, (float) Math.abs(sizeS1 - sizeS2) + 1));

        // Fill cost matrix with max values
        for (float [] f : costMatrix) {
            Arrays.fill(f, Float.MAX_VALUE);
        }

        // Set first value to zero
        costMatrix[0][0] = 0f;

        //TODO: This part is in every cDTW implementation I found online, but I don't think it changes the result whatsoever...?
        //for (int k = 1; k < sizeS1; k++) {
        //    for (int l = Math.max(1, k-w); l < Math.min(sizeS2, k+w); l++) {
        //        costMatrix[k][l] = 0f;
        //    }
        //}

        // Calculate minimal warping distance
        for (int i = 1; i < sizeS1; i++){
            for (int j = Math.max(1, i-w); j < Math.min(sizeS2, i+w); j++){
                float distance = s1.get(i).distanceTo(s2.get(j));
                costMatrix[i][j] = distance + min( costMatrix[i-1][j], costMatrix[i][j-1], costMatrix[i-1][j-1] );
            }
        }

        // Undo Padding
        s1.remove(0);
        s2.remove(0);

        // Return last value of the cost matrix
        return costMatrix [sizeS1-1] [sizeS2-1];
    }

}
