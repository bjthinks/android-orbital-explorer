package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    // private static final String TAG = "OrbitalRenderer";

    private AssetManager mAssetManager;
    private Integrate mIntegrate;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Context context) {
        mAssetManager = context.getAssets();
        mIntegrate = new Integrate();
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mIntegrate.newContext(mAssetManager);
    }

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    }

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
    }
}
