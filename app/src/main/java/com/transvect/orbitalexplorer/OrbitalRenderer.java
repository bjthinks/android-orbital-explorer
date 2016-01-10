package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OrbitalRenderer";

    private OrbitalView mOrbitalView;
    private AssetManager mAssetManager;
    private Integrator mIntegrator;
    private ScreenDrawer mScreenDrawer;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Context context) {
        mOrbitalView = orbitalView;
        mAssetManager = context.getAssets();
        mIntegrator = new Integrator(context);
        mScreenDrawer = new ScreenDrawer();
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mIntegrator.newContext(mAssetManager);
        mScreenDrawer.newContext(mAssetManager);
    }

    private float mAspectRatio = 1.0f;

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mAspectRatio = (float) width / (float) height;

        final double scaleDownFactor = 4.0;
        int smallWidth = (int) (width / scaleDownFactor);
        int smallHeight = (int) (height / scaleDownFactor);
        mIntegrator.resize(smallWidth, smallHeight);
        mScreenDrawer.resize(smallWidth, smallHeight, width, height);
    }

    private long then = 0;

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mIntegrator.render(shaderTransform);
        mScreenDrawer.render(mIntegrator.getTexture());

        boolean LOG_FPS = true;
        if (LOG_FPS) {
            long now = System.currentTimeMillis();
            Log.d(TAG, "Frame time " + (now - then) + "ms");
            then = now;
        }
    }
}
