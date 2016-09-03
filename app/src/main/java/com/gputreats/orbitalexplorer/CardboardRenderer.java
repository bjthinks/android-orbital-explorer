package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.Matrix;

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
        orbital = ((CardboardActivity) context).getOrbital();
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

    private HeadTransform head;
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        orbitalTextures.loadOrbital(orbital);
        head = headTransform;
    }

    @Override
    public void onDrawEye(Eye eye) {
        int type = eye.getType();

        Integrator integrator;
        if (type == Eye.Type.LEFT)
            integrator = integratorLeft;
        else // RIGHT or MONOCULAR
            integrator = integratorRight;

        float[] scaleMatrix = new float[16];
        float scaleFactor = 1f / (4f * orbitalTextures.getRadius());
        scaleMatrix[0] = scaleFactor;
        scaleMatrix[5] = scaleFactor;
        scaleMatrix[10] = scaleFactor;
        scaleMatrix[15] = 1;

        float[] translateMatrix = new float[16];
        float[] headForward = new float[3];
        head.getForwardVector(headForward, 0);
        translateMatrix[0] = 1;
        translateMatrix[5] = 1;
        translateMatrix[10] = 1;
        translateMatrix[15] = 1;
        translateMatrix[12] = headForward[0] / 2f;
        translateMatrix[13] = headForward[1] / 2f;
        translateMatrix[14] = headForward[2] / 2f;

        float temp1[] = new float[16];
        Matrix.multiplyMM(temp1, 0, translateMatrix, 0, scaleMatrix, 0);

        float[] eyeViewMatrix = eye.getEyeView();
        float[] temp2 = new float[16];
        Matrix.multiplyMM(temp2, 0, eyeViewMatrix, 0, temp1, 0);

        float[] projectionMatrix = eye.getPerspective(1f, 2f);
        float[] transform = new float[16];
        Matrix.multiplyMM(transform, 0, projectionMatrix, 0, temp2, 0);

        float inverseTransform[] = new float[16];
        Matrix.invertM(inverseTransform, 0, transform, 0);

        Texture integratorOutput = integrator.render(orbitalTextures,
                inverseTransform, /* TODO */ true);

        screenDrawer.render(orbitalTextures, integratorOutput, null, eye.getViewport());
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onRendererShutdown() {
    }
}
