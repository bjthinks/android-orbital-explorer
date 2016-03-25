package com.gputreats.orbitalexplorer;

public class Vector3 {

    private final double x, y, z;

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public Vector3(double xx, double yy, double zz) {
        x = xx;
        y = yy;
        z = zz;
    }

    // Primitive operations

    /* public Vector3 add(Vector3 rhs) {
        return new Vector3(x + rhs.x, y + rhs.y, z + rhs.z);
    } */

    public Vector3 multiply(double c) {
        return new Vector3(x * c, y * c, z * c);
    }

    public double dot(Vector3 rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    // Composite operations

    /* public Vector3 negate() {
        return multiply(-1.0);
    } */

    /* public Vector3 subtract(Vector3 rhs) {
        return add(rhs.negate());
    } */

    public Vector3 divide(double c) {
        return multiply(1.0 / c);
    }

    public double normSquared() {
        return dot(this);
    }

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public Vector3 normalize() {
        return divide(norm());
    }
}
