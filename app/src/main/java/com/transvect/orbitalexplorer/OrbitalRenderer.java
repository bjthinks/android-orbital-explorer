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

    private Controller mController;
    private AssetManager mAssetManager;
    private DemoRenderStage mDemoRenderStage;
    private FinalRenderStage mFinalRenderStage;
    private int mWidth;
    private int mHeight;

    OrbitalRenderer(Controller controller, AssetManager assetManager) {
        mController = controller;
        mAssetManager = assetManager;
        mDemoRenderStage = new DemoRenderStage();
        mFinalRenderStage = new FinalRenderStage();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // For pessimistic testing
        // GLES30.glDisable(GLES30.GL_DITHER);
        mDemoRenderStage.newContext(mAssetManager);
        mFinalRenderStage.newContext(mAssetManager);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mWidth = width;
        mHeight = height;
        mDemoRenderStage.resize(mWidth, mHeight);
        mFinalRenderStage.resize(mWidth, mHeight);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mController.computeShaderTransform(mWidth, mHeight);
        mDemoRenderStage.render(shaderTransform);
        mFinalRenderStage.render(mDemoRenderStage.getTexture());
    }
}
