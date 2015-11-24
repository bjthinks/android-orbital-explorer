package com.transvect.orbitalexplorer;

import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

public class Camera implements Parcelable {
    // private static final String TAG = "Camera";

    public Camera() {
    }

    public Camera(double cameraDistance, Quaternion totalRotation) {
        mCameraDistance = cameraDistance;
        mTotalRotation = totalRotation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(mCameraDistance);
        out.writeParcelable(mTotalRotation, flags);
    }

    public static final Parcelable.Creator<Camera> CREATOR
            = new Parcelable.Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in.readDouble(),
                    (Quaternion) in.readParcelable(Quaternion.class.getClassLoader()));
        }
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    private double mCameraDistance = 16.0;
    private static final double MIN_CAMERA_DISTANCE = 1.0;
    private static final double MAX_CAMERA_DISTANCE = 32.0;

    // Two finger zoom by an incremental size ratio of f
    public void zoom(double f) {
        mCameraDistance /= f;
        if (mCameraDistance < MIN_CAMERA_DISTANCE)
            mCameraDistance = MIN_CAMERA_DISTANCE;
        if (mCameraDistance > MAX_CAMERA_DISTANCE)
            mCameraDistance = MAX_CAMERA_DISTANCE;
    }

    private Quaternion mTotalRotation = Quaternion.rotation(Math.PI / 2.0, X_HAT);
    private static final Vector3 X_HAT = new Vector3(1, 0, 0);
    private static final Vector3 Y_HAT = new Vector3(0, 1, 0);
    private static final Vector3 Z_HAT = new Vector3(0, 0, 1);

    // One finger drag by an increment of (x,y) pixels
    // x and y are multiples of the (mean) screen size
    public void drag(double x, double y) {
        // Finger moves right --> positive rotation about y axis
        Quaternion y_rotation = Quaternion.rotation(Math.PI * x, Y_HAT);
        // Finger moves up --> negative rotation about x axis
        Quaternion x_rotation = Quaternion.rotation(-Math.PI * y, X_HAT);
        // total = normalize(x_rot * y_rot * total)
        mTotalRotation = x_rotation.multiply(y_rotation).multiply(mTotalRotation);
        mTotalRotation = mTotalRotation.normalize();
    }

    // Two finger twist by an angle increment of theta
    public void twist(double theta) {
        Quaternion z_rotation = Quaternion.rotation(theta, Z_HAT);
        mTotalRotation = z_rotation.multiply(mTotalRotation);
        mTotalRotation = mTotalRotation.normalize();
    }

    private Vector2 mFlingVelocity = new Vector2(0.0, 0.0);
    private boolean stillFlinging = false;
    // Maximum half-turns per second
    private static final double MAX_FLING_SPEED = 6.0;
    // Fraction of total speed lost per second
    private static final double FLING_SLOWDOWN_LINEAR = 0.5;
    // Seconds before stopping, if there were no linear slowdown
    private static final double MAX_FLING_TIME = 5.0;
    // Absolute amount of speed lost per second
    private static final double FLING_SLOWDOWN_CONSTANT = MAX_FLING_SPEED / MAX_FLING_TIME;

    public void fling(double x, double y) {
        // x and y are multiples of the mean screen size per second
        mFlingVelocity = new Vector2(x, y);
        double flingSpeed = mFlingVelocity.norm();
        if (flingSpeed > MAX_FLING_SPEED)
            mFlingVelocity = mFlingVelocity.multiply(MAX_FLING_SPEED / flingSpeed);
        stillFlinging = true;
    }

    public boolean continueFling() {
        if (stillFlinging) {
            // TODO use actual FPS
            drag(mFlingVelocity.getX() / 60.0, mFlingVelocity.getY() / 60.0);
            mFlingVelocity = mFlingVelocity.multiply(1 - FLING_SLOWDOWN_LINEAR / 60.0);
            if (mFlingVelocity.norm() < FLING_SLOWDOWN_CONSTANT / 60.0) {
                stopFling();
            } else {
                Vector2 flingDirection = mFlingVelocity.normalize();
                Vector2 velocityReduction = flingDirection.multiply(FLING_SLOWDOWN_CONSTANT / 60.0);
                mFlingVelocity = mFlingVelocity.subtract(velocityReduction);
            }
        }
        return stillFlinging;
    }

    public void stopFling() {
        mFlingVelocity = new Vector2(0.0, 0.0);
        stillFlinging = false;
    }

    public float[] computeShaderTransform(double aspectRatio) {
        float ratio = (float) Math.sqrt(aspectRatio);
        float near = (float) Math.max(1.0f, mCameraDistance - 16.0);
        float far = (float) (mCameraDistance + 16.0);
        float leftRight = near * ratio;
        float bottomTop = near / ratio;
        float[] projectionMatrix = new float[16];
        Matrix.frustumM(projectionMatrix, 0,
                -leftRight, leftRight,
                -bottomTop, bottomTop,
                near, far);

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, (float) (-mCameraDistance), 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        float[] viewProjMatrix = new float[16];
        Matrix.multiplyMM(viewProjMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        float[] cameraRotation = mTotalRotation.asRotationMatrix();

        float[] shaderTransform = new float[16];
        Matrix.multiplyMM(shaderTransform, 0, viewProjMatrix, 0, cameraRotation, 0);

        return shaderTransform;
    }
}
