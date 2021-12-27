package com.meicke.threeSpaceSensorAndroidAPI;

import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Immutable Quaternion class. Represents values as w + xi + yj + zk.
 */
public class Quaternion {

    private final float w, x, y, z;

    /**
     * Public constructor for quaternions.
     * @param w Real / scalar component.
     * @param x First imaginary / vector component.
     * @param y Second imaginary / vector component.
     * @param z Third imaginary / vector component.
     */
    public Quaternion ( float w, float x, float y, float z ) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a quaternion from a string representation in the form w + xi + yj + zk.
     * Compatible with the output of the toString of this class.
     * @param string Quaternion as string.
     * @return Quaternion instance.
     */
    public static Quaternion parseString (String string) {

        String[] parts = string.split("\\+");
        parts[1] = parts[1].replaceFirst("i", "");
        parts[2] = parts[2].replaceFirst("j", "");
        parts[3] = parts[3].replaceFirst("k", "");

        Float w = Float.valueOf(parts[0]);
        Float x = Float.valueOf(parts[1]);
        Float y = Float.valueOf(parts[2]);
        Float z = Float.valueOf(parts[3]);

        return new Quaternion(w, x, y, z);
    }

    /**
     * Returns the real / scalar component of the quaternion.
     * @return w
     */
    public float getW () {
        return w;
    }

    /**
     * Returns the first imaginary / vector component of the quaternion.
     * @return x
     */
    public float getX () {
        return x;
    }

    /**
     * Returns the second imaginary / vector component of the quaternion.
     * @return y
     */
    public float getY () {
        return y;
    }

    /**
     * Returns the third imaginary / vector component of the quaternion.
     * @return z
     */
    public float getZ () {
        return z;
    }

    /**
     * Returns the string representation of the quaternion in the form of w + xi + yj + zk.
     * @return String representation of the quaternion
     */
    public String toString () {
        return w + " + " + x + "i + " + y + "j + " + z + "k";
    }

    /**
     * Compares the components of two quaternions.
     * @return True if all quaternion components are equal.
     */
    public boolean equals (Quaternion q) {
        if (q == null) return false;
        else return w == q.w && x == q.x && y == q.y && z == q.z;
    }

    /**
     * Transforms the quaternion into a byte array representation of length 16.
     * @return Byte array [16] of the quaternion.
     */
    public byte[] toBytes () {
        return ByteBuffer.allocate(16)
                .putFloat(0, x).putFloat(4, y).putFloat(8, z).putFloat(12, w).array();
    }

    /**
     * Transforms the quaternion into a float array representation of length 4.
     * @return Float array [4] of the quaternion.
     */
    public float[] toFloatArray () {
        return new float[]{this.x, this.y, this.z, this.w};
    }

    /**
     * Computes the norm of the quaternion.
     * @return Quaternion norm as float.
     */
    public float getNorm () {
        return (float) Math.sqrt( w*w + x*x + y*y + z*z );
    }

    /**
     * Returns only the vector part of the quaternion as float vector.
     * @return Quaternion vector part as float vector.
     */
    public Vector<Float> getVectorPart () {
        Vector<Float> vectorPart = new Vector<>(3);
        vectorPart.add(getX());
        vectorPart.add(getY());
        vectorPart.add(getZ());
        return vectorPart;
    }

    /**
     * Returns the complex conjugation of the quaternion.
     * @return Conjugation of the quaternion.
     */
    public Quaternion conjugate () {
        return new Quaternion( w, -x, -y, -z );
    }

    /**
     * Returns the inverse of the quaternion.
     * @return Inverse of the quaternion.
     */
    public Quaternion inverse() {
        float d = w*w + x*x + y*y + z*z;
        return new Quaternion( w/d, -x/d, -y/d, -z/d );
    }

    /**
     * Returns the natural logarithm of the quaternion. (Implementation is based on code from
     * the python library "numpy.quaternion".)
     * @return Logarithm of the quaternion
     */
    public Quaternion log () {
        final float quatLowerBound = 1E-14f;

        float vectorLength = (float) Math.sqrt(x*x + y*y + z*z);

        if (vectorLength <= quatLowerBound * Math.abs(w)) {
            if (w < 0) {
                if (Math.abs(w + 1) > quatLowerBound) {
                    return new Quaternion((float) Math.log(-w), (float) Math.PI, 0, 0);
                } else {
                    return new Quaternion(0, (float) Math.PI, 0, 0);
                }
            } else {
                return new Quaternion((float) Math.log(w), 0, 0, 0);
            }
        } else {
            float arctangent = (float) Math.atan2(vectorLength, w);
            float scale = arctangent / vectorLength;
            return new Quaternion((float) (Math.log(w * w + vectorLength * vectorLength) / 2f), x * scale, y * scale, z * scale);
        }
    }

    /**
     * Computes the sum of two quaternions.
     * @param q Second quaternion.
     * @return Sum of both quaternions.
     */
    public Quaternion add ( Quaternion q ) {
        return new Quaternion( this.w + q.w, this.x + q.x, this.y + q.y, this.z + q.z );
    }

    /**
     * Computes the product of two quaternions.
     * @param q Second quaternion.
     * @return Product of both quaternions.
     */
    public Quaternion mult ( Quaternion q ) {
        float new_w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        float new_x = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        float new_y = this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x;
        float new_z = this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w;
        return new Quaternion ( new_w, new_x, new_y, new_z );
    }

    /**
     * Computes the result of this quaternion divided by quaternion q.
     * @param q Divisor.
     * @return Result of the division.
     */
    public Quaternion div ( Quaternion q) {
        return this.mult(q.inverse());
    }

    public Quaternion vectorDiv (float f) {
        if (f == 0) throw new IllegalArgumentException();
        return new Quaternion(w/f, x/f, y/f, z/f);
    }

    /**
     * Checks if this quaternion is a unit quaternion (Is it a legal rotation value?).
     * Considers limited precision of float values.
     * @return True if the quaternion norm is 1.
     */
    public boolean isUnitQuaternion () {
        return isUnitQuaternion(0.00001f);
    }

    /**
     * Checks if this quaternion is a unit quaternion with a tolerance value.
     * @return True if the quaternion norm is 1.
     */
    public boolean isUnitQuaternion (float tolerance) {
        return Math.abs(1 - getNorm()) < tolerance;
    }

    /**
     * Calculates the distance between two quaternions using the arccos-function.
     * This method is significantly faster, but slightly less accurate than the logDistanceTo function.
     * @param q Second quaternion.
     * @return Distance as float.
     */
    public float distanceTo ( Quaternion q ) {
        float innerProduct = this.w * q.w + this.x * q.x + this.y * q.y + this.z * q.z;

        // Due to rounding errors the acos parameter can sometimes be greater than 1 or smaller than -1.
        // This leads to an output of NaN. Therefore the checkValue variable is used to limit its range.
        float checkValue = 2 * (innerProduct * innerProduct) - 1;
        if (checkValue > 1) checkValue = 1f;
        if (checkValue < -1) checkValue = -1f;

        return (float) Math.acos( checkValue );
    }

    /**
     * Calculates the distance between two quaternions using the quaternion log function.
     * This method is significantly slower, but slightly more accurate than the distanceTo function.
     * @param q Second quaternion.
     * @return Distance as float.
     */
    public float logDistanceTo ( Quaternion q ) {

        // This should be enough according to Park, F.C.; Ravani, Bahram (1997). "Smooth invariant interpolation of rotations".
        return 2 * this.inverse().mult(q).log().getNorm();

        // But in "Quaternion Dynamic Time Warping" by B. Jablonski this one is used, which is basically twice the amount of computations (!?)
        //return 2 * Math.min(this.inverse().mult(q).log().getNorm(),q.inverse().mult(this).log().getNorm());
    }

    /**
     * Converts the quaternion to an euler angle representation.
     * @return Rotation of the quaternion as euler angle vector (Roll, Pitch, Yaw).
     */
    public Vector<Float> convertToEulerAngles() {
        Vector<Float> eulerAngles = new Vector<Float>(3);

        // Roll
        float sinR_cosP = 2 * ( w * x + y * z );
        float cosR_cosP = 1 - 2 * ( x * x + y * y );
        eulerAngles.add( (float) Math.atan2( sinR_cosP, cosR_cosP ) );

        // Pitch
        float sinP = 2 * ( w * y - z * x );
        if ( Math.abs(sinP) >= 1 ) {
            eulerAngles.add( (float) Math.copySign(Math.PI / 2, sinP) );
        } else {
            eulerAngles.add( (float) Math.asin(sinP) );
        }

        // Yaw
        float sinY_cosP = 2 * ( w * z + x * y );
        float cosY_cosP = 1 - 2 * ( y * y + z * z );
        eulerAngles.add( (float) Math.atan2( sinY_cosP, cosY_cosP ) );

        return eulerAngles;
    }

    /**
     * Converts the rotation of the quaternion to a rotation matrix.
     * @return Rotation matrix as two dimensional float array.
     */
    public float [] [] convertToRotationMatrix () {
        float [][] result = new float [3] [3];

        float ww = getW() * getW();
        float wx = getW() * getX();
        float wy = getW() * getY();
        float wz = getW() * getZ();
        float xx = getX() * getX();
        float xy = getX() * getY();
        float xz = getX() * getZ();
        float yy = getY() * getY();
        float yz = getY() * getZ();
        float zz = getZ() * getZ();

        // first line
        // result [0] [0] = ww + xx - yy - zz;
        // result [0] [1] = 2 * ( xy + wz );
        // result [0] [2] = 2 * ( xz - wy );

        // second line
        // result [1] [0] = 2 * ( xy - wz );
        // result [1] [1] = ww - xx + yy - zz;
        // result [1] [2] = 2 * ( wx + yz );

        // third line
        // result [2] [0] = 2 * ( wy + xz );
        // result [2] [1] = 2 * ( yz - wx );
        // result [2] [2] = ww - xx - yy + zz;

        // first row
        result [0] [0] = ww + xx - yy - zz;
        result [1] [0] = 2 * ( xy + wz );
        result [2] [0] = 2 * ( xz - wy );

        // second row
        result [0] [1] = 2 * ( xy - wz );
        result [1] [1] = ww - xx + yy - zz;
        result [2] [1] = 2 * ( wx + yz );

        // third row
        result [0] [2] = 2 * ( wy + xz );
        result [1] [2] = 2 * ( yz - wx );
        result [2] [2] = ww - xx - yy + zz;

        return result;
    }
}