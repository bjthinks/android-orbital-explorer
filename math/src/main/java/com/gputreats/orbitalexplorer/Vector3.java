package com.gputreats.orbitalexplorer;

class Vector3 {

    private final double x, y, z;

    double getX() { return x; }
    double getY() { return y; }
    double getZ() { return z; }

    Vector3(double inX, double inY, double inZ) {
        x = inX;
        y = inY;
        z = inZ;
    }

    // Primitive operations

    /* Vector3 add(Vector3 rhs) {
        return new Vector3(x + rhs.x, y + rhs.y, z + rhs.z);
    } */

    Vector3 multiply(double c) {
        return new Vector3(x * c, y * c, z * c);
    }

    double dot(Vector3 rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    // Composite operations

    /* Vector3 negate() {
        return multiply(-1.0);
    } */

    /* Vector3 subtract(Vector3 rhs) {
        return add(rhs.negate());
    } */

    Vector3 divide(double c) {
        return multiply(1.0 / c);
    }

    double normSquared() {
        return dot(this);
    }

    double norm() {
        return Math.sqrt(normSquared());
    }

    Vector3 normalize() {
        return divide(norm());
    }
}
