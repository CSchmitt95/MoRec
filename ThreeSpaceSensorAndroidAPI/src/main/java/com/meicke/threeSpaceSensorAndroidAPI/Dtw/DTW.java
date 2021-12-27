package com.meicke.threeSpaceSensorAndroidAPI.Dtw;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.Arrays;
import java.util.List;

/**
 * Dynamic Time Warping class. Constructs a full cost matrix to compare two quaternion time series.
 * Test results show a motion recognition capability of 98.46%. This is the slow, naive approach
 * to dynamic time warping, which will however always find the best solution for a matching.
 */
public class DTW extends AbstractDTW {

    /**
     * Computes the warp distance between the parameters s1 and s2.
     * @param s1 First quaternion time series.
     * @param s2 Second quaternion time series.
     * @return Warp distance between both parameters as float value.
     */
    public float getWarpDistance (List<Quaternion> s1, List<Quaternion> s2) {

        // Pad time series (padding the 0th row and column is easier than coding a multi condition
        // if statement to avoid out of bounds errors for the first values)
        Quaternion padding = new Quaternion(1,0,0,0);
        s1.add(0, padding);
        s2.add(0, padding);

        // Create cost matrix
        int sizeS1 = s1.size();
        int sizeS2 = s2.size();
        float [] [] costMatrix = new float [sizeS1] [sizeS2];

        // Fill cost matrix with max values
        for (float [] f : costMatrix) {
            Arrays.fill(f, Float.MAX_VALUE);
        }

        // Set first value to zero
        costMatrix[0][0] = 0f;

        // Calculate minimal warping distance
        for (int i = 1; i < sizeS1; i++){
            for (int j = 1; j < sizeS2; j++){
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
