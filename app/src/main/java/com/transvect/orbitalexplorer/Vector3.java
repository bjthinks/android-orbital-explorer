package com.transvect.orbitalexplorer;

public class Vector3 {

    private final double x, y, z;

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    Vector3(double xx, double yy, double zz) {
        x = xx;
        y = yy;
        z = zz;
    }

    Vector3 multiply(double c) {
        return new Vector3(x * c, y * c, z * c);
    }

    Vector3 divide(double c) {
        return new Vector3(x / c, y / c, z / c);
    }

    /* Vector3 negate() {
        return new Vector3(-x, -y, -z);
    } */

    double norm() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    Vector3 normalize() {
        return divide(norm());
    }
}
