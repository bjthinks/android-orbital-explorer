package com.gputreats.orbitalexplorer;

import android.content.Context;

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
        orbital = new Orbital(1, 4, 2, 1, false, true);
        orbitalTextures = new OrbitalTextures(context);
    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        orbitalTextures.onSurfaceCreated();
    }


    @Override
    public void onSurfaceChanged(int width, int height) {
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        orbitalTextures.loadOrbital(orbital);
    }

    @Override
    public void onDrawEye(Eye eye) {
        int type = eye.getType();
        if (type == Eye.Type.LEFT) {
        } else if (type == Eye.Type.RIGHT) {
        } else { // MONOCULAR
        }

        float[] eyeView = eye.getEyeView();
        float[] perspective = eye.getPerspective(1.0f, 2.0f);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onRendererShutdown() {
    }
}
