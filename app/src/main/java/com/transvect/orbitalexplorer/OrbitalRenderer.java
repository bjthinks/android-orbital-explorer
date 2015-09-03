package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.Matrix;

/**
 * The OpenGL guts start here.
 */

public class OrbitalRenderer extends MyGLRenderer {

    private static final String TAG = "OrbitalRenderer";

    private DemoRenderStage mDemoRenderStage;
    private FinalRenderStage mFinalRenderStage;

    private int mWidth, mHeight;

    private final float[] mProjectionMatrix = new float[16];

    private static Quaternion mThisFrameRotation = new Quaternion(1);
    private static Quaternion mRotationalMomentum = new Quaternion(1);
    private static Quaternion mTotalRotation = new Quaternion(1);

    public void rotateBy(Quaternion r)
    {
        if (mThisFrameRotation != null)
            mThisFrameRotation = r.multiply(mThisFrameRotation);
        else
            mThisFrameRotation = r;
    }

    private AssetManager assetManager;

    OrbitalRenderer(Context context) {
        assetManager = context.getAssets();
        mDemoRenderStage = new DemoRenderStage();
        mFinalRenderStage = new FinalRenderStage();
    }

    @Override
    public void onCreate(int width, int height, boolean contextIsNew) {
        if (contextIsNew) {
            mDemoRenderStage.newContext(assetManager);
            mFinalRenderStage.newContext(assetManager);
        }
        mWidth = width;
        mHeight = height;
        float ratio = (float) Math.sqrt((double) width / (double) height);
        float leftRight = ratio;
        float bottomTop = 1.0f / ratio;
        float near = 1.0f;
        float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0,
                -leftRight, leftRight,
                -bottomTop, bottomTop,
                near, far);
    }

    private float[] computeShaderTransform() {

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        float[] viewProjMatrix = new float[16];
        Matrix.multiplyMM(viewProjMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0);

        float[] cameraRotation = mTotalRotation.asRotationMatrix();

        float[] shaderTransform = new float[16];
        Matrix.multiplyMM(shaderTransform, 0, viewProjMatrix, 0, cameraRotation, 0);

        return shaderTransform;
    }

    @Override
    public void onDrawFrame() {
        // TODO this should take into account the # of milliseconds between calls
        if (mThisFrameRotation != null) {
            mRotationalMomentum = mThisFrameRotation;
            mThisFrameRotation = null;
        } else {
            mRotationalMomentum = mRotationalMomentum.pow(0.99);
        }
        mTotalRotation = mRotationalMomentum.multiply(mTotalRotation);

        float[] shaderTransform = computeShaderTransform();
        mDemoRenderStage.render(mWidth, mHeight, shaderTransform);
        mFinalRenderStage.render(mWidth, mHeight, mDemoRenderStage.getTextureId());
    }
}
