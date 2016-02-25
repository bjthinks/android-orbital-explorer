package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "OrbitalRenderer";

    private int dpi;
    private OrbitalView orbitalView;
    private Integrator integrator;
    private ScreenDrawer screenDrawer;
    private RenderState renderState;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView_, Context context, RenderState renderState_) {
        orbitalView = orbitalView_;
        dpi = context.getResources().getDisplayMetrics().densityDpi;
        renderState = renderState_;
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
    }

    // Main thread
    public void orbitalChanged(Orbital newOrbital) {
        integrator.orbitalChanged(newOrbital);
    }

    // Main thread
    public void colorChanged(boolean c) {
        renderState.setColor(c);
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        integrator.newContext();
        screenDrawer.newContext();
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
        double scaleDownFactor = performanceScalingFactor * 160.0 / Math.max(160.0, dpi);
        int integrationWidth  = (int) (scaleDownFactor * mWidth);
        int integrationHeight = (int) (scaleDownFactor * mHeight);
        integrator.resize(integrationWidth, integrationHeight);
        screenDrawer.resize(integrationWidth, integrationHeight, mWidth, mHeight);
        Log.d(TAG, "Resize, screen " + mWidth + " x " + mHeight
                + ", integration " + integrationWidth + " x " + integrationHeight);
    }

    private long then = 0;
    private double recentPerformance = 0.0;

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        RenderState.FrozenState frozenState = renderState.freeze();

        float[] shaderTransform = orbitalView.getNextTransform(mAspectRatio);
        Texture integratorOutput = integrator.render(shaderTransform, frozenState);
        screenDrawer.render(integratorOutput, frozenState);

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
