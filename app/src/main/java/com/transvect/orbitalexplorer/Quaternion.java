package com.transvect.orbitalexplorer;

/**
 * Immutable quaternions
 */

public class Quaternion {
    private final double r, i, j, k;

    public Quaternion(double real, double imaginary, double jmaginary, double kmaginary) {
        r = real;
        i = imaginary;
        j = jmaginary;
        k = kmaginary;
    }

    public Quaternion(double real, Vector3 unreal) {
        r = real;
        i = unreal.getX();
        j = unreal.getY();
        k = unreal.getZ();
    }

    public Quaternion(double real) {
        r = real;
        i = 0;
        j = 0;
        k = 0;
    }

    Quaternion multiply(Quaternion y) {
        return new Quaternion(
                r * y.r - i * y.i - j * y.j - k * y.k,
                r * y.i + i * y.r + j * y.k - k * y.j,
                r * y.j - i * y.k + j * y.r + k * y.i,
                r * y.k + i * y.j - j * y.i + k * y.r);
    }

    Quaternion multiply(double y) {
        return new Quaternion(y * r, y * i, y * j, y * k);
    }

    double norm() {
        return Math.sqrt(r*r + i*i + j*j + k*k);
    }

    static Quaternion rotation(double angle, Vector3 x) {
        x = x.normalize();
        double s = Math.sin(angle / 2);
        double c = Math.cos(angle / 2);
        return new Quaternion(c, x.multiply(s));
    }

    float[] asRotationMatrix() {
        float[] result = new float[16];
        result[0] = (float) (r*r + i*i - j*j - k*k);
        result[1] = (float) (2*r*k + 2*i*j);
        result[2] = (float) (-2*r*j + 2*i*k);
        result[3] = 0f;
        result[4] = (float) (-2*r*k + 2*i*j);
        result[5] = (float) (r*r - i*i + j*j - k*k);
        result[6] = (float) (2*r*i + 2*j*k);
        result[7] = 0f;
        result[8] = (float) (2*r*j + 2*i*k);
        result[9] = (float) (-2*r*i + 2*j*k);
        result[10] = (float) (r*r - i*i - j*j + k*k);
        result[11] = 0f;
        result[12] = 0f;
        result[13] = 0f;
        result[14] = 0f;
        result[15] = 1f;
        return result;
    }
}
