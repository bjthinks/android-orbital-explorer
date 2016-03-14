package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {

    private int dpi;
    private Integrator integrator;
    private ScreenDrawer screenDrawer;
    private RenderState renderState;
    private QuadratureCurves quadratureCurves; // DEV

    // Main thread
    public OrbitalRenderer(Context context) {
        try {
            renderState = ((RenderStateProvider) context).provideRenderState();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RenderStateProvider");
        }
        dpi = context.getResources().getDisplayMetrics().densityDpi;
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
        quadratureCurves = new QuadratureCurves(context); // DEV
    }

    // Rendering thread
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        integrator.newContext();
        screenDrawer.newContext();
        quadratureCurves.newContext(); // DEV
    }

    private float aspectRatio = 1.0f;

    // Rendering thread
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        aspectRatio = (float) width / (float) height;
        double scaleDownFactor = 160.0 / Math.max(160.0, dpi);
        int integrationWidth  = (int) (scaleDownFactor * width);
        int integrationHeight = (int) (scaleDownFactor * height);
        integrator.resize(integrationWidth, integrationHeight);
        screenDrawer.resize(integrationWidth, integrationHeight, width, height);
        quadratureCurves.resize(width, height); // DEV
    }

    // Rendering thread
    @Override
    public void onDrawFrame(GL10 unused) {
        RenderState.FrozenState frozenState = renderState.freeze(aspectRatio);

        Texture integratorOutput = integrator.render(frozenState);
        screenDrawer.render(integratorOutput, frozenState);
        quadratureCurves.render(frozenState); // DEV
    }
}
