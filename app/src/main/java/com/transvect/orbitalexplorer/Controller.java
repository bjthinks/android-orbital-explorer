package com.transvect.orbitalexplorer;

import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

public class Controller {
    private static final String TAG = "Controller";

    private final double flingScale;
    private Quaternion mRotationalMomentum = new Quaternion(1.0);
    private Quaternion mTotalRotation = new Quaternion(1.0);
    private double mCameraDistance = 3.0;

    // Keys for storing state in Bundle
    private static final String totalRotationName = "totalRotation";
    private static final String cameraDistanceName = "cameraDistance";

    public Controller(Bundle savedState) {
        // TODO calculate something sensible here; should be constant * pixeldensity
        flingScale = 1.0 / 60000.0;
        if (savedState != null) {
            mCameraDistance = savedState.getDouble(cameraDistanceName);
            mTotalRotation = savedState.getParcelable(totalRotationName);
        }
    }

    public void saveState(Bundle outState) {
        outState.putDouble(cameraDistanceName, mCameraDistance);
        outState.putParcelable(totalRotationName, mTotalRotation);
    }

    public synchronized void drag(double x, double y) {
        Quaternion xz_rotation = Quaternion.rotation(Math.PI * x, new Vector3(0, 1, 0));
        Quaternion yz_rotation = Quaternion.rotation(Math.PI * y, new Vector3(-1, 0, 0));
        mTotalRotation = yz_rotation.multiply(xz_rotation).multiply(mTotalRotation);
    }

    public synchronized void fling(double x, double y) {
        Quaternion xz_rotation = Quaternion.rotation(Math.PI * x * flingScale, new Vector3(0, 1, 0));
        Quaternion yz_rotation = Quaternion.rotation(Math.PI * y * flingScale, new Vector3(-1, 0, 0));
        mRotationalMomentum = yz_rotation.multiply(xz_rotation);
    }

    public synchronized void stopFling() {
        mRotationalMomentum = new Quaternion(1.0);
    }

    public synchronized void spin(double theta) {
        Quaternion xy_rotation = Quaternion.rotation(theta, new Vector3(0, 0, 1));
        mTotalRotation = xy_rotation.multiply(mTotalRotation);
    }

    public synchronized void zoom(double f) {
        mCameraDistance /= f;
        if (mCameraDistance > 10.0)
            mCameraDistance = 10.0;
        if (mCameraDistance < 0.5)
            mCameraDistance = 0.5;
    }

    public synchronized float[] computeShaderTransform(float aspectRatio) {
        mTotalRotation = mRotationalMomentum.multiply(mTotalRotation);
        // TODO handle the case when this becomes too small to matter
        mRotationalMomentum = mRotationalMomentum.pow(0.99);

        float ratio = (float) Math.sqrt(aspectRatio);
        float near = 0.25f;
        float far = (float) (mCameraDistance + 1.0);
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
