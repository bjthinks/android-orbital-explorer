package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The OpenGL guts start here.
 */

public class OrbitalRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "OrbitalRenderer";

    private DemoRenderStage mDemoRenderStage;
    private FinalRenderStage mFinalRenderStage;

    private int mWidth, mHeight;

    // TODO save these as preferences
    private static Quaternion mTotalRotation = new Quaternion(1.0);
    private static double mCameraDistance = 3.0;

    public void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }

    public void scaleBy(double f) {
        mCameraDistance /= f;
        if (mCameraDistance > 10.0)
            mCameraDistance = 10.0;
        if (mCameraDistance < 2.0)
            mCameraDistance = 2.0;
    }

    private AssetManager mAssetManager;

    OrbitalRenderer(AssetManager assetManager) {
        mWidth = -1;
        mHeight = -1;

        mAssetManager = assetManager;
        mDemoRenderStage = new DemoRenderStage();
        mFinalRenderStage = new FinalRenderStage();
    }

    private float[] computeShaderTransform() {

        float ratio = (float) Math.sqrt((double) mWidth / (double) mHeight);
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

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = computeShaderTransform();
        mDemoRenderStage.render(shaderTransform);
        mFinalRenderStage.render(mDemoRenderStage.getTexture());
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mWidth = -1;
        mHeight = -1;

        // For pessimistic testing
        // GLES30.glDisable(GLES30.GL_DITHER);
        mDemoRenderStage.newContext(mAssetManager);
        mFinalRenderStage.newContext(mAssetManager);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if (width == mWidth && height == mHeight)
            return;

        mWidth = width;
        mHeight = height;

        mDemoRenderStage.resize(mWidth, mHeight);
        mFinalRenderStage.resize(mWidth, mHeight);
    }
}
