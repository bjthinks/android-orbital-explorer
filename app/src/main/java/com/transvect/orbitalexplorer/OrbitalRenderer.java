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
    private ColorModel mColorModel;
    private Enlarge mEnlarge;
    private float mAspectRatio = 1.0f;

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

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        final int scaleDownFactor = 4;
        mAspectRatio = (float) width / (float) height;
        mIntegrate .resize(width / scaleDownFactor, height / scaleDownFactor);
        mColorModel.resize(width / scaleDownFactor, height / scaleDownFactor);
        mEnlarge.resize(width, height);
    }

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mIntegrate.render(shaderTransform);
        mColorModel.render(mIntegrate.getTexture());
        mEnlarge.render(mColorModel.getTexture());
    }
}
