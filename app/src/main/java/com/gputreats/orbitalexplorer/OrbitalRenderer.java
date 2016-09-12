package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class OrbitalRenderer implements GLSurfaceView.Renderer {

    private final OrbitalTextures orbitalTextures;
    private final Integrator integrator;
    private final ScreenDrawer screenDrawer;
    private final RenderState renderState;
    private final FPS fps;

    // Main thread

    OrbitalRenderer(Context context) {
        renderState = ((RenderStateProvider) context).provideRenderState();
        orbitalTextures = new OrbitalTextures(context);
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
        fps = new FPS();
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

    private double aspectRatio = 1.0;
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        try {
            aspectRatio = width / (double) height;
            int integrationWidth  = width / 3;
            int integrationHeight = height / 3;
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
            if (BuildConfig.DEBUG)
                fps.frame();
            RenderState.FrozenState frozenState = renderState.freeze(aspectRatio);
            orbitalTextures.loadOrbital(frozenState.orbital);
            Texture integratorOutput = integrator.render(orbitalTextures,
                    frozenState.inverseTransform, frozenState.needToIntegrate);
            screenDrawer.render(orbitalTextures, integratorOutput,
                    frozenState.screenGrabRequested ? frozenState.screenGrabHandler : null, null);
        } catch (RuntimeException e) {
            renderState.reportRenderException(e);
        }
    }
}
