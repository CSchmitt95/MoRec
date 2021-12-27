package com.meicke.threeSpaceSensorAndroidAPI;

import org.junit.Test;

/**
 * This test file was abused to quickly experiment with quaternion functions during development.
 * Please ignore.
 */
public class QuaternionUnitTest {

    @Test
    public void checkRotationAngleDifferences() {

        // Some manually crafted quaternions
        Quaternion a = new Quaternion(1, 0, 0, 0);
        Quaternion b = new Quaternion(0.71f, 0.71f, 0, 0);
        Quaternion c = new Quaternion(0, 1, 0, 0);
        Quaternion d = new Quaternion(0.16f, -0.88f, -0.39f, 0.22f);

        // Some broken (non unit-) quaternions
        Quaternion e = Quaternion.parseString("-2.9407284E-4 + 7.915E-42i + -9.513927E-4j + -2.911586E-4k");
        Quaternion f = Quaternion.parseString("-5.0119637E-4 + 2.334E-41i + 0.0011318867j + -1.1779374E-4k");

        // Some more correct quaternions
        Quaternion g = Quaternion.parseString("0.99999833 + -9.2109357E-4i + -5.8135844E-4j + -0.0013351366k");
        Quaternion h = Quaternion.parseString("0.99999774 + 0.0016949509i + 0.0011520804j + 1.3655396E-4k");
        Quaternion i = Quaternion.parseString("0.99938637 + 0.010120936i + -0.009042451j + -0.032283045k");

        System.out.println("Difference a -> b: " + a.distanceTo(b));
        System.out.println("Log-Difference a -> b: " + a.logDistanceTo(b));
        System.out.println("Difference b -> c: " + b.distanceTo(c));
        System.out.println("Log-Difference b -> c:" + b.logDistanceTo(c));
        System.out.println("Difference a -> c: " + a.distanceTo(c));
        System.out.println("Log-Difference a -> c: " + a.logDistanceTo(c));

        System.out.println("Log of " + b.toString() + ": " + b.log().toString());
        System.out.println("Log of " + d.toString() + ": " + d.log().toString());

        System.out.println("Difference a to b: " + a.add(b.inverse()).getNorm());
        System.out.println("Difference a to c: " + a.add(c.inverse()).getNorm());

        System.out.println("Norm e: " + e.getNorm());
        System.out.println("Norm f: " + f.getNorm());
        System.out.println("Difference e -> f: " + e.distanceTo(f));
        System.out.println("Log-Difference e -> f: " + e.logDistanceTo(f));
        System.out.println("Difference f -> e: " + f.distanceTo(e));
        System.out.println("Log-Difference f -> e: " + f.logDistanceTo(e));

        System.out.println("Norm g: " + g.getNorm());
        System.out.println("Norm h: " + h.getNorm());
        System.out.println("Norm i: " + i.getNorm());

        System.out.println("Difference h -> i: " + h.distanceTo(i));
        System.out.println("Log-Difference h -> i: " + h.logDistanceTo(i));
        System.out.println("Difference i -> h: " + i.distanceTo(h));
        System.out.println("Log-Difference i -> h: " + i.logDistanceTo(h));

    }

}