package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class OrbitalRenderer implements GLSurfaceView.Renderer {

    private final OrbitalData orbitalData;
    private final Integrator integrator;
    private final ScreenDrawer screenDrawer;
    private final RenderState renderState;
    private final FPS fps;

    // Main thread

    OrbitalRenderer(Context context) {
        renderState = ((RenderStateProvider) context).provideRenderState();
        orbitalData = new OrbitalData(context);
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
        fps = new FPS();
    }

    // Rendering thread

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        orbitalData.onSurfaceCreated();
        integrator.onSurfaceCreated();
        screenDrawer.onSurfaceCreated();
    }

    // Rendering thread

    private double aspectRatio = 1.0;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        aspectRatio = (double) width / (double) height;
        int integrationWidth  = width / 3;
        int integrationHeight = height / 3;
        integrator.resize(integrationWidth, integrationHeight);
        screenDrawer.resize(integrationWidth, integrationHeight, width, height);
    }

    // Rendering thread

    @Override
    public void onDrawFrame(GL10 gl) {
        if (BuildConfig.DEBUG)
            fps.frame();
        FrozenState frozenState = renderState.freeze(aspectRatio);
        orbitalData.loadOrbital(frozenState.orbital);
        Texture integratorOutput = integrator.render(orbitalData,
                frozenState.inverseTransform, frozenState.needToIntegrate);
        screenDrawer.render(orbitalData, integratorOutput, null);
    }
}
