package com.transvect.orbitalexplorer;

import android.os.Parcel;
import android.os.Parcelable;
// import android.util.Log;

public class Quaternion implements Parcelable {
    // public static final String TAG = "Quaternion";

    // Parcelable crud
    private Quaternion(Parcel in) {
        r = in.readDouble();
        i = in.readDouble();
        j = in.readDouble();
        k = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(r);
        out.writeDouble(i);
        out.writeDouble(j);
        out.writeDouble(k);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Quaternion> CREATOR
            = new Parcelable.Creator<Quaternion>() {
        @Override
        public Quaternion createFromParcel(Parcel in) {
            return new Quaternion(in);
        }
        public Quaternion[] newArray(int size) {
            return new Quaternion[size];
        }
    };

    private final double r, i, j, k;

    /* public double real() { return r; }
    public double imag() { return i; }
    public double jmag() { return j; }
    public double kmag() { return k; } */

    /* public Vector3 unreal() {
        return new Vector3(i, j, k);
    } */

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

    /* Quaternion multiply(double c) {
        return new Quaternion(r * c, i * c, j * c, k * c);
    } */

    Quaternion divide(double c) {
        return new Quaternion(r / c, i / c, j / c, k / c);
    }

    double norm() {
        return Math.sqrt(r * r + i * i + j * j + k * k);
    }

    Quaternion normalize() {
        return divide(norm());
    }

    /* Quaternion pow(double alpha) {
        Quaternion normalized = normalize();
        double normalizedRealPart = normalized.real();
        Vector3 normalizedUnrealPart = normalized.unreal();
        double c = normalizedRealPart;
        double s = normalizedUnrealPart.norm();
        double theta = Math.acos(c);
        double alphaTheta = alpha * theta;
        double ac = Math.cos(alphaTheta);
        Quaternion normalizedExponential;
        if (s > 0.001) {
            double as = Math.sin(alphaTheta);
            normalizedExponential = new Quaternion(ac, normalizedUnrealPart.multiply(as / s));
        } else {
            normalizedExponential = new Quaternion(ac, normalizedUnrealPart.multiply(alpha));
        }
        return normalizedExponential.normalize().multiply(Math.pow(norm(), alpha));
    } */

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
