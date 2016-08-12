package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {

    private int screenDpi;
    private OrbitalTextures orbitalTextures;
    private Integrator integrator;
    private ScreenDrawer screenDrawer;
    private RenderState renderState;

    // Main thread

    public OrbitalRenderer(Context context) {
        try {
            renderState = ((RenderStateProvider) context).provideRenderState();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RenderStateProvider");
        }
        screenDpi = context.getResources().getDisplayMetrics().densityDpi;
        orbitalTextures = new OrbitalTextures(context);
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
    }

    // Rendering thread

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        try {
            orbitalTextures.onSurfaceCreated();
            integrator.onSurfaceCreated();
            screenDrawer.onSurfaceCreated();
        } catch (RuntimeException e) {
            renderState.reportRenderException(e);
        }
    }

    // Rendering thread

    private float aspectRatio = 1.0f;

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        try {
            aspectRatio = (float) width / (float) height;
            double integrationDpi = 160.0;
            double scaleDownFactor = integrationDpi / Math.max(integrationDpi, screenDpi);
            int integrationWidth  = (int) (scaleDownFactor * width);
            int integrationHeight = (int) (scaleDownFactor * height);
            integrator.resize(integrationWidth, integrationHeight);
            screenDrawer.resize(integrationWidth, integrationHeight, width, height);
        } catch (RuntimeException e) {
            renderState.reportRenderException(e);
        }
    }

    // Rendering thread

    @Override
    public void onDrawFrame(GL10 unused) {
        try {
            FPS.frame();
            RenderState.FrozenState frozenState = renderState.freeze(aspectRatio);
            orbitalTextures.loadOrbital(frozenState.orbital);
            Texture integratorOutput = integrator.render(frozenState, orbitalTextures);
            screenDrawer.render(integratorOutput, frozenState);
        } catch (RuntimeException e) {
            renderState.reportRenderException(e);
        }
    }
}
