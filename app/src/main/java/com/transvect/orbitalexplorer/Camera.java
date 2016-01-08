package com.transvect.orbitalexplorer;

import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

public class Camera implements Parcelable {
    // private static final String TAG = "Camera";

    public Camera() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
    }

    public static final Parcelable.Creator<Camera> CREATOR
            = new Parcelable.Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera();
        }
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    // Two finger zoom by an incremental size ratio of f
    public void zoom(double f) {
    }

    // One finger drag by an increment of (x,y) pixels
    // x and y are multiples of the (mean) screen size
    public void drag(double x, double y) {
    }

    // Two finger twist by an angle increment of theta
    public void twist(double theta) {
    }

    public void fling(double x, double y) {
    }

    public boolean continueFling() {
        return true;
    }

    public void stopFling() {
    }

    public float[] computeShaderTransform(double aspectRatio) {
        return new float[16];
    }
}
