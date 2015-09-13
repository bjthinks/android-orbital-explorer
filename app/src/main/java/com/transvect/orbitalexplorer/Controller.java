package com.transvect.orbitalexplorer;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

public class Controller {
    private static final String TAG = "Controller";

    private Vector2 mFlingVelocity = new Vector2(0.0, 0.0);
    private Quaternion mTotalRotation = new Quaternion(1.0);
    private double mCameraDistance = 3.0;
    private OrbitalView mOrbitalView = null;

    // Keys for storing state in Bundle
    private static final String totalRotationName = "totalRotation";
    private static final String cameraDistanceName = "cameraDistance";

    public Controller(OrbitalView orbitalView, Bundle savedState) {
        mOrbitalView = orbitalView;

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
        // x and y are multiples of the mean screen size
        Quaternion xz_rotation = Quaternion.rotation(Math.PI * x, new Vector3(0, 1, 0));
        Quaternion yz_rotation = Quaternion.rotation(Math.PI * y, new Vector3(-1, 0, 0));
        mTotalRotation = yz_rotation.multiply(xz_rotation).multiply(mTotalRotation);
    }

    private static final double MAX_FLING_SPEED = 6.0; // half-turns per second
    private static final double MAX_FLING_TIME = 5.0; // seconds before stopping
    private static final double FLING_SLOWDOWN = MAX_FLING_SPEED / MAX_FLING_TIME;

    public synchronized void fling(double x, double y) {
        // x and y are multiples of the mean screen size per second
        mFlingVelocity = new Vector2(x, y);
        double flingSpeed = mFlingVelocity.norm();
        if (flingSpeed > MAX_FLING_SPEED)
            mFlingVelocity = mFlingVelocity.multiply(MAX_FLING_SPEED / flingSpeed);
        mOrbitalView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void flingDecay() {
        if (mFlingVelocity.norm() < FLING_SLOWDOWN / 60.0) {
            stopFling();
        } else {
            Vector2 flingDirection = mFlingVelocity.normalize();
            Vector2 velocityReduction = flingDirection.multiply(FLING_SLOWDOWN / 60.0);
            mFlingVelocity = mFlingVelocity.subtract(velocityReduction);
        }
    }

    public synchronized void stopFling() {
        mFlingVelocity = new Vector2(0.0, 0.0);
        mOrbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
        // TODO use actual FPS here and in flingDecay
        drag(mFlingVelocity.getX() / 60.0, mFlingVelocity.getY() / 60.0);
        flingDecay();

        float ratio = (float) Math.sqrt(aspectRatio);
        float near = (float) Math.max(0.5, mCameraDistance - 3.0);
        float far = (float) (mCameraDistance + 3.0);
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
