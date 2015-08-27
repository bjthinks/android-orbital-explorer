package com.transvect.orbitalexplorer;

/**
 * Immutable quaternions
 */

public class Quaternion {
    private final double r, i, j, k;

    public Quaternion(double re, double im, double jm, double km) {
        r = re;
        i = im;
        j = jm;
        k = km;
    }

    public Quaternion(double re) {
        r = re;
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

    static Quaternion rotation(double angle, double x, double y, double z) {
        double n = Math.sqrt(x*x + y*y + z*z);
        x /= n;
        y /= n;
        z /= n;
        double s = Math.sin(angle / 2);
        double c = Math.cos(angle / 2);
        return new Quaternion(c, s * x, s * y, s * z);
    }

    float[] asFloatMatrix() {
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
