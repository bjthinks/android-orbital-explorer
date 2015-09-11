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

    public synchronized void drag(double x, double y) {
        double rotx = Math.PI * x;
        double roty = Math.PI * y;
        Quaternion xz_rotation = Quaternion.rotation(rotx, new Vector3(0, 1, 0));
        Quaternion yz_rotation = Quaternion.rotation(roty, new Vector3(-1, 0, 0));
        Quaternion composite = yz_rotation.multiply(xz_rotation);
        rotateBy(composite);
    }

    public synchronized void spin(double theta) {
        Quaternion xy_rotation = Quaternion.rotation(theta, new Vector3(0, 0, 1));
        rotateBy(xy_rotation);
    }

    private void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }

    public synchronized float[] computeShaderTransform(float aspectRatio) {
        float ratio = (float) Math.sqrt(aspectRatio);
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
