package com.transvect.orbitalexplorer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OrbitalRenderer";

    private DisplayMetrics mMetrics;
    private OrbitalView mOrbitalView;
    private AssetManager mAssetManager;
    private Integrator mIntegrator;
    private ScreenDrawer mScreenDrawer;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Activity context) {
        mMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        Log.d(TAG, "densityDpi = " + mMetrics.densityDpi);

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

    private int mWidth = 1;
    private int mHeight = 1;
    private float mAspectRatio = 1.0f;
    private double performanceScalingFactor = 1.0;

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mWidth = width;
        mHeight = height;
        mAspectRatio = (float) width / (float) height;
        resizeIntegration();
    }

    private void resizeIntegration() {
        double scaleDownFactor = performanceScalingFactor * 160.0
                / Math.max(160.0, mMetrics.densityDpi);
        int integrationWidth  = (int) (scaleDownFactor * mWidth);
        int integrationHeight = (int) (scaleDownFactor * mHeight);
        mIntegrator.resize(integrationWidth, integrationHeight);
        mScreenDrawer.resize(integrationWidth, integrationHeight, mWidth, mHeight);
        Log.d(TAG, "Resize, screen " + mWidth + " x " + mHeight
                + ", integration " + integrationWidth + " x " + integrationHeight);
    }

    private long then = 0;
    private double recentPerformance = 0.0;

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] shaderTransform = mOrbitalView.getNextTransform(mAspectRatio);
        mIntegrator.render(shaderTransform);
        mScreenDrawer.render(mIntegrator.getTexture());

        long now = System.currentTimeMillis();
        int milliseconds = (int) (now - then);
        then = now;

        int frameGoodness = 20 - milliseconds; // minus infinity to about plus 3 (@ 60 FPS)
        if (frameGoodness >= 0)
            recentPerformance += 3.0 * (double) frameGoodness;
        else
            recentPerformance -= Math.log((double) -frameGoodness);

        if (recentPerformance < -30.0) {
            if (performanceScalingFactor > 0.125) {
                performanceScalingFactor /= Math.pow(2.0, 0.125);
                if (performanceScalingFactor < 0.125)
                    performanceScalingFactor = 0.125;
                resizeIntegration();
            }
            recentPerformance = 0.0;
        }

        if (recentPerformance > 30.0) {
            if (performanceScalingFactor < 1.0) {
                performanceScalingFactor *= Math.pow(2.0, 0.125);
                if (performanceScalingFactor > 1.0)
                    performanceScalingFactor = 1.0;
                resizeIntegration();
            }
            recentPerformance = 0.0;
        }
    }
}
