package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

class CardboardRenderer implements GvrView.StereoRenderer {

    private Orbital orbital;
    private int screenDpi;
    private OrbitalTextures orbitalTextures;
    private Integrator integratorLeft, integratorRight;
    private ScreenDrawer screenDrawer;

    CardboardRenderer(Context context) {
        screenDpi = context.getResources().getDisplayMetrics().densityDpi;
        orbital = new Orbital(1, 4, 2, 1, false, true);
        orbitalTextures = new OrbitalTextures(context);
        integratorLeft = new Integrator(context);
        integratorRight = new Integrator(context);
        screenDrawer = new ScreenDrawer(context);
    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        orbitalTextures.onSurfaceCreated();
        integratorLeft.onSurfaceCreated();
        integratorRight.onSurfaceCreated();
        screenDrawer.onSurfaceCreated();
    }


    @Override
    public void onSurfaceChanged(int width, int height) {
        double integrationDpi = 160.0;
        double scaleDownFactor = integrationDpi / Math.max(integrationDpi, screenDpi);
        int integrationWidth  = (int) (scaleDownFactor * width);
        int integrationHeight = (int) (scaleDownFactor * height);
        integratorLeft.resize(integrationWidth, integrationHeight);
        integratorRight.resize(integrationWidth, integrationHeight);
        screenDrawer.resize(integrationWidth, integrationHeight, width, height);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        orbitalTextures.loadOrbital(orbital);
    }

    @Override
    public void onDrawEye(Eye eye) {
        int type = eye.getType();
        Integrator integrator;
        if (type == Eye.Type.LEFT)
            integrator = integratorLeft;
        else // RIGHT or MONOCULAR
            integrator = integratorRight;

        //float[] eyeView = eye.getEyeView();
        float[] projection = eye.getPerspective(1.0f, 2.0f);

        float inverseTransform[] = new float[16];
        Matrix.invertM(inverseTransform, 0, projection, 0);

        Texture integratorOutput = integrator.render(orbitalTextures,
                inverseTransform, /* TODO */ true);
        screenDrawer.render(integratorOutput, /* FIXME */ 4, orbitalTextures.getColor(), null);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onRendererShutdown() {
    }
}
