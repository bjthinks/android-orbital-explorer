package com.gputreats.orbitalexplorer;

import android.os.Parcel;
import android.os.Parcelable;

class Quaternion implements Parcelable {

    @SuppressWarnings("MethodReturnAlwaysConstant")
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(r);
        parcel.writeDouble(i);
        parcel.writeDouble(j);
        parcel.writeDouble(k);
    }

    @SuppressWarnings({"AnonymousInnerClassWithTooManyMethods", "AnonymousInnerClass"})
    public static final Parcelable.Creator<Quaternion> CREATOR
            = new Parcelable.Creator<Quaternion>() {
        @Override
        public Quaternion createFromParcel(Parcel parcel) {
            return new Quaternion(
                    parcel.readDouble(), parcel.readDouble(),
                    parcel.readDouble(), parcel.readDouble());
        }
        @Override
        public Quaternion[] newArray(int size) {
            return new Quaternion[size];
        }
    };

    private final double r, i, j, k;

    Quaternion(double real, double imaginary, double jmaginary, double kmaginary) {
        r = real;
        i = imaginary;
        j = jmaginary;
        k = kmaginary;
    }

    Quaternion(double real, Vector3 unreal) {
        r = real;
        i = unreal.getX();
        j = unreal.getY();
        k = unreal.getZ();
    }

    /* public Quaternion(double real) {
        r = real;
        i = 0;
        j = 0;
        k = 0;
    } */

    private Quaternion add(Quaternion y) {
        return new Quaternion(r + y.r, i + y.i, j + y.j, k + y.k);
    }

    private Quaternion subtract(Quaternion y) {
        return add(y.multiply(-1.0));
    }

    Quaternion multiply(Quaternion y) {
        return new Quaternion(
                r * y.r - i * y.i - j * y.j - k * y.k,
                r * y.i + i * y.r + j * y.k - k * y.j,
                r * y.j - i * y.k + j * y.r + k * y.i,
                r * y.k + i * y.j - j * y.i + k * y.r);
    }

    private Quaternion multiply(double c) {
        return new Quaternion(r * c, i * c, j * c, k * c);
    }

    private Quaternion divide(double c) {
        return multiply(1.0 / c);
    }

    private double norm() {
        return Math.sqrt(r * r + i * i + j * j + k * k);
    }

    Quaternion normalize() {
        return divide(norm());
    }

    double dist(Quaternion y) {
        return subtract(y).norm();
    }

    float[] asRotationMatrix() {
        float[] result = new float[16];
        result[0] = (float) (r * r + i * i - j * j - k * k);
        result[1] = (float) (2.0 * r * k + 2.0 * i * j);
        result[2] = (float) (-2.0 * r * j + 2.0 * i * k);
        result[3] = 0.0f;
        result[4] = (float) (-2.0 * r * k + 2.0 * i * j);
        result[5] = (float) (r * r - i * i + j * j - k * k);
        result[6] = (float) (2.0 * r * i + 2.0 * j * k);
        result[7] = 0.0f;
        result[8] = (float) (2.0 * r * j + 2.0 * i * k);
        result[9] = (float) (-2.0 * r * i + 2.0 * j * k);
        result[10] = (float) (r * r - i * i - j * j + k * k);
        result[11] = 0.0f;
        result[12] = 0.0f;
        result[13] = 0.0f;
        result[14] = 0.0f;
        result[15] = 1.0f;
        return result;
    }
}
