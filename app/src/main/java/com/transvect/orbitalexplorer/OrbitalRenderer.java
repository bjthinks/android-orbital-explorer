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
    private Integrate mIntegrate;
    private ColorModel mColorModel;
    private Enlarge mEnlarge;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Context context) {
        mOrbitalView = orbitalView;
        mAssetManager = context.getAssets();
        mIntegrate = new Integrate(context);
        mColorModel = new ColorModel();
        mEnlarge = new Enlarge();
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mIntegrate.newContext(mAssetManager);
        mColorModel.newContext(mAssetManager);
        mEnlarge.newContext(mAssetManager);
    }

    private float mAspectRatio = 1.0f;

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mAspectRatio = (float) width / (float) height;

        final double scaleDownFactor = 256.0;
        int smallWidth = (int) (width / scaleDownFactor);
        int smallHeight = (int) (height / scaleDownFactor);
        mIntegrate.resize(smallWidth, smallHeight);
        mColorModel.resize(smallWidth, smallHeight);
        mEnlarge.resize(width, height);
    }

    private long then = 0;

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mIntegrate.render(shaderTransform);
        mColorModel.render(mIntegrate.getTexture());
        mEnlarge.render(mColorModel.getTexture());

        boolean LOG_FPS = true;
        if (LOG_FPS) {
            long now = System.currentTimeMillis();
            Log.d(TAG, "Frame time " + (now - then) + "ms");
            then = now;
        }
    }
}
