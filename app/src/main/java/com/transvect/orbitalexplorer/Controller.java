package com.transvect.orbitalexplorer;

import android.opengl.Matrix;

/**
 * Created by bwj on 9/11/15.
 */
public class Controller {

    // TODO save these as preferences
    private static double mCameraDistance = 3.0;
    private static Quaternion mTotalRotation = new Quaternion(1.0);

    public synchronized void scaleBy(double f) {
        mCameraDistance /= f;
        if (mCameraDistance > 10.0)
            mCameraDistance = 10.0;
        if (mCameraDistance < 2.0)
            mCameraDistance = 2.0;
    }

    public synchronized void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }
    
    public synchronized float[] computeShaderTransform(int width, int height) {
        float ratio = (float) Math.sqrt((double) width / (double) height);
        float leftRight = ratio;
        float bottomTop = 1.0f / ratio;
        float near = 1.0f;
        float far = (float) (mCameraDistance + 1.0);
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
