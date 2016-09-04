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

        float[] forward = new float[3];
        float[] right = new float[3];
        head.getForwardVector(forward, 0);
        head.getRightVector(right, 0);

        float[] lateral = new float[3];
        if (type == Eye.Type.LEFT) {
            integrator = integratorLeft;
            lateral = right;
        } else { // RIGHT or MONOCULAR
            integrator = integratorRight;
            lateral = new float[3];
            for (int i = 0; i < 3; ++i)
                lateral[i] = -right[i];
        }

        float[] scaleMatrix = new float[16];
        float scaleFactor = 1f / orbitalTextures.getRadius();
        scaleMatrix[0] = scaleFactor;
        scaleMatrix[5] = scaleFactor;
        scaleMatrix[10] = scaleFactor;
        scaleMatrix[15] = 1;

        float distanceToNucleus = 1.5f;
        float halfEyeDistance = 0.15f;
        float[] translateMatrix = new float[16];
        translateMatrix[0] = 1;
        translateMatrix[5] = 1;
        translateMatrix[10] = 1;
        translateMatrix[15] = 1;
        translateMatrix[12] = distanceToNucleus * forward[0] + halfEyeDistance * lateral[0];
        translateMatrix[13] = distanceToNucleus * forward[1] + halfEyeDistance * lateral[1];
        translateMatrix[14] = distanceToNucleus * forward[2] + halfEyeDistance * lateral[2];

        float temp1[] = new float[16];
        Matrix.multiplyMM(temp1, 0, translateMatrix, 0, scaleMatrix, 0);

        float[] headViewMatrix = head.getHeadView();
        // TODO needs cleanup, not really right API call
        headViewMatrix[12] = 0;
        headViewMatrix[13] = 0;
        headViewMatrix[14] = 0;
        float[] temp2 = new float[16];
        Matrix.multiplyMM(temp2, 0, headViewMatrix, 0, temp1, 0);

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
