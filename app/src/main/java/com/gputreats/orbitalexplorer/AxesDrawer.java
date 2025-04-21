package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

public class AxesDrawer {

    final FloatBuffer axes;
    private final AssetManager assets;
    private final AppPreferences appPreferences;
    private Program program;

    AxesDrawer(Context context) {
        float[] axesCoordinates = {
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f
        };
        axes = FloatBufferFactory.make(axesCoordinates);
        assets = context.getAssets();
        appPreferences = new AppPreferences(context);
    }

    public void onSurfaceCreated() {
        MyGL.checkGLES();
        program = new Program(assets, "axes.vert", "axes.frag");
    }

    private int width, height;

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public void render(OrbitalData orbitalData /* Does this have maxRadius?*/, float[] transform) {
        MyGL.checkGLES();

        program.use();
        GLES30.glViewport(0, 0, width, height);
    }
}
