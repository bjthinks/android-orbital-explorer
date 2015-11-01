package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
// import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    // private static final String TAG = "OrbitalRenderer";

    private OrbitalView mOrbitalView;
    private AssetManager mAssetManager;
    private DemoRenderStage mDemoRenderStage;
    private FinalRenderStage mFinalRenderStage;
    private float mAspectRatio = 1.0f;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, AssetManager assetManager) {
        mOrbitalView = orbitalView;
        mAssetManager = assetManager;
        mDemoRenderStage = new DemoRenderStage();
        mFinalRenderStage = new FinalRenderStage();
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // For pessimistic testing
        // GLES30.glDisable(GLES30.GL_DITHER);
        mDemoRenderStage.newContext(mAssetManager);
        mFinalRenderStage.newContext(mAssetManager);
    }

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mAspectRatio = (float) width / (float) height;
        mDemoRenderStage.resize(width, height);
        mFinalRenderStage.resize(width, height);
    }

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mDemoRenderStage.render(shaderTransform);
        mFinalRenderStage.render(mDemoRenderStage.getTexture());
    }
}
