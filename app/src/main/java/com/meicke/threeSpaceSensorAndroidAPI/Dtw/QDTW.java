package com.meicke.threeSpaceSensorAndroidAPI.Dtw;

import com.meicke.threeSpaceSensorAndroidAPI.Quaternion;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Quaternion Dynamic Time Warping class. This approach is based on the publication "Quaternion
 * Dynamic Time Warping" from B. Jablonski. It utilises a distance measure, the local derivation
 * and the local curvature of the trajectory. However this implementation has produced very bad
 * test results! The most likely broken aspect of this code is the curvature function. This code
 * is not recommended for result orientated use!
 */
public class QDTW extends AbstractDTW {

    /**
     * Computes the warp distance between the parameters s1 and s2.
     * @param s1 First quaternion time series.
     * @param s2 Second quaternion time series.
     * @return Warp distance between both parameters as float value.
     */
    public float getWarpDistance (List<Quaternion> s1, List<Quaternion> s2) {

        // Create cost matrix
        int sizeS1 = s1.size();
        int sizeS2 = s2.size();
        float [] [] costMatrix = new float [sizeS1] [sizeS2];

        // Fill cost matrix with max values
        for (float [] f : costMatrix) {
            Arrays.fill(f, Float.MAX_VALUE);
        }

        // Set first value to zero
        costMatrix[0] [0] = 0f;

        // Calculate minimal warping distance
        for (int i = 1; i < sizeS1-1; i++){
            for (int j = 1; j < sizeS2-1; j++){

                //TODO: Performance issue: "Successor" and "current" Quaternion should be reused in the next cycle as "current" and "predecessor"...
                Quaternion quatI = s1.get(i);
                Quaternion quatJ = s2.get(j);
                Quaternion quatISucc = s1.get(i+1);
                Quaternion quatJSucc = s2.get(j+1);
                Quaternion quatIPred = s1.get(i-1);
                Quaternion quatJPred = s2.get(j-1);

                float qDist = s1.get(i).logDistanceTo(s2.get(j));
                float qDerv = 2 * quatI.inverse().mult(quatISucc).inverse().mult(quatJ.inverse().mult(quatJSucc)).log().getNorm();

                Vector<Float> kappa_i = kappaFunction(quatI, quatISucc, quatIPred);
                Vector<Float> kappa_j = kappaFunction(quatJ, quatJSucc, quatJPred);
                Vector<Float> diff = new Vector<>(3);
                diff.add(kappa_i.get(0) - kappa_j.get(0));
                diff.add(kappa_i.get(1) - kappa_j.get(1));
                diff.add(kappa_i.get(2) - kappa_j.get(2));

                float qCurv = (float) Math.sqrt(diff.get(0)*diff.get(0)+diff.get(1)*diff.get(1)+diff.get(2)*diff.get(2));

                costMatrix[i][j] = qDist * qDerv * qCurv + min( costMatrix[i-1] [j],
                                                                costMatrix[i] [j-1],
                                                                costMatrix[i-1] [j-1]);
            }
        }

        // Return "last" value of the cost matrix (It's not the actual last value, since those cannot be calculated.
        // The last computable value is at [n-1, m-1].)
        return costMatrix [sizeS1-2] [sizeS2-2];
    }

    /**
     * Kappa function from B. Jablonskis QDTW algorithm
     * @param quat Current quaternion.
     * @param quatSucc Successor of the current quaternion.
     * @param quatPred Predecessor of the current quaternion.
     * @return Temporary result from the kappa function.
     */
    private static Vector<Float> kappaFunction (Quaternion quat, Quaternion quatSucc, Quaternion quatPred) {

        Vector<Float> omegaQuat = omegaFunction(quat, quatSucc);
        Vector<Float> omegaQuatPred = omegaFunction(quatPred, quat);

        Vector<Float> result = new Vector<>(3);
        result.add(omegaQuat.get(0) - omegaQuatPred.get(0));
        result.add(omegaQuat.get(1) - omegaQuatPred.get(1));
        result.add(omegaQuat.get(2) - omegaQuatPred.get(2));

        return  result;
    }

    /**
     * Omega function from B. Jablonskis QDTW algorithm
     * @param quat Current quaternion.
     * @param quatSucc Successor of the current quaternion.
     * @return Temporary result from the omega function.
     */
    private static Vector<Float> omegaFunction (Quaternion quat, Quaternion quatSucc) {
        Quaternion p = quat.inverse().mult(quatSucc).log();
        float p_norm = p.getNorm();
        Vector<Float> result = new Vector<>(3);
        if (p_norm != 0) {
            result.add(p.getX() / p_norm);
            result.add(p.getY() / p_norm);
            result.add(p.getZ() / p_norm);
        } else {
            result.add(0f);
            result.add(0f);
            result.add(0f);
        }
        return result;
    }

}
