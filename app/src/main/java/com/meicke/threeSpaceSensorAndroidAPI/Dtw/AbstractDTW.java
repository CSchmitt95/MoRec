package com.meicke.threeSpaceSensorAndroidAPI.Dtw;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.List;

/**
 * Abstract class including all variants of dynamic time warping
 */
public abstract class AbstractDTW {

    /**
     * Computes the warp distance between both parameters.
     * @param s1 First quaternion time series.
     * @param s2 Second quaternion time series.
     * @return Warp distance between both parameters as float value.
     */
    public abstract float getWarpDistance (List<Quaternion> s1, List<Quaternion> s2);

    /**
     * Returns the smallest of value of the three input parameters.
     * @param first First float value.
     * @param second Second float value.
     * @param third Third float value.
     * @return Smallest value of the input parameters.
     */
    protected float min (float first, float second, float third) {
        return Math.min(first, Math.min(second, third));
    }

}
