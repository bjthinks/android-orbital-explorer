package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    // private static final String TAG = "OrbitalRenderer";

    private OrbitalView mOrbitalView;
    private AssetManager mAssetManager;
    private Integrate mIntegrate;
    private float mAspectRatio = 1.0f;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Context context) {
        mOrbitalView = orbitalView;
        mAssetManager = context.getAssets();
        mIntegrate = new Integrate(context);
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mIntegrate.newContext(mAssetManager);
    }

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mAspectRatio = (float) width / (float) height;

        final double scaleDownFactor = 4.0;
        int smallWidth = (int) (width / scaleDownFactor);
        int smallHeight = (int) (height / scaleDownFactor);
        mIntegrate.resize(smallWidth, smallHeight);
    }

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mIntegrate.render(shaderTransform);
    }
}
