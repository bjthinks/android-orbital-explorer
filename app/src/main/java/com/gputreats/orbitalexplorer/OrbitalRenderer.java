package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class OrbitalRenderer implements GLSurfaceView.Renderer {

    private Orbital orbital;
    private final AppPreferences appPreferences;
    private final OrbitalData orbitalData;
    private final Integrator integrator;
    private final ScreenDrawer screenDrawer;
    private final AxesDrawer axesDrawer;
    private final OrbitalView orbitalView;
    private final FPS fps;
    private long frozenTime;

    // Main thread

    OrbitalRenderer(Context context, OrbitalView ov) {
        appPreferences = new AppPreferences(context);
        orbitalData = new OrbitalData(context);
        integrator = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
        axesDrawer = new AxesDrawer(context);
        orbitalView = ov;
        fps = new FPS();
        frozenTime = 0;
    }

    // Main thread

    synchronized void onOrbitalChanged(Orbital newOrbital, long freezeTime) {
        orbital = newOrbital;
        frozenTime = freezeTime;
    }

    // Rendering thread

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        orbitalData.onSurfaceCreated();
        integrator.onSurfaceCreated();
        screenDrawer.onSurfaceCreated();
        axesDrawer.onSurfaceCreated();
    }

    // Rendering thread

    private double aspectRatio = 1.0;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        aspectRatio = (double) width / (double) height;
        int scaleDownFactor = appPreferences.getUltraQuality() ? 1 : 3;
        int integrationWidth  = width / scaleDownFactor;
        int integrationHeight = height / scaleDownFactor;
        integrator.resize(integrationWidth, integrationHeight);
        screenDrawer.resize(integrationWidth, integrationHeight, width, height);
        axesDrawer.resize(width, height);
    }

    // Rendering thread

    @Override
    public void onDrawFrame(GL10 gl) {
        Orbital orbitalTemp;
        synchronized (this) {
            orbitalTemp = orbital;
        }
        if (orbitalTemp != null) {
            if (BuildConfig.DEBUG)
                fps.frame();
            orbitalData.loadOrbital(orbitalTemp);
            float[] transform = orbitalView.getTransform(aspectRatio);
            // Samsung Galaxy S5 can't invert 4x4 matrices correctly in the OpenGL driver,
            // so we make the CPU do the inverse instead.
            float[] inverseTransform = new float[16];
            Matrix.invertM(inverseTransform, 0, transform, 0);
            Texture integratorOutput = integrator.render(orbitalData, inverseTransform);
            screenDrawer.render(orbitalData, integratorOutput,
                    frozenTime > 0 ? frozenTime : System.currentTimeMillis());
            axesDrawer.render(orbitalData, transform);
        }
    }
}
