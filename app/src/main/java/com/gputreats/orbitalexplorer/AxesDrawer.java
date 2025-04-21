package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

public class AxesDrawer {

    final FloatBuffer axes, colors;
    private final AssetManager assets;
    private final AppPreferences appPreferences;
    private Program program;

    AxesDrawer(Context context) {
        float[] axesCoordinates = {
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
        };
        axes = FloatBufferFactory.make(axesCoordinates);
        float[] axesColors = {
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
        };
        colors = FloatBufferFactory.make(axesColors);
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

        if (!appPreferences.getShowAxes())
            return;

        program.use();
        boolean savedDepthTest = GLES30.glIsEnabled(GLES30.GL_DEPTH_TEST);
        boolean savedScissorTest = GLES30.glIsEnabled(GLES30.GL_SCISSOR_TEST);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_SCISSOR_TEST);
        GLES30.glViewport(0, 0, width, height);

        int projectionMatrixHandle = program.getUniformLocation("projectionMatrix");
        GLES30.glUniformMatrix4fv(projectionMatrixHandle, 1, false, transform, 0);

        int inPositionHandle = program.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 3, GLES30.GL_FLOAT, false,
                12, axes);

        int inColorHandle = program.getAttribLocation("inColor");
        GLES30.glEnableVertexAttribArray(inColorHandle);
        GLES30.glVertexAttribPointer(inColorHandle, 3, GLES30.GL_FLOAT, false,
                12, colors);

        GLES30.glLineWidth(3.0f); // TODO compute pixels per inch and calculate this
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, 6);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        GLES30.glDisableVertexAttribArray(inColorHandle);
        if (savedDepthTest)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        if (savedScissorTest)
            GLES30.glEnable(GLES30.GL_SCISSOR_TEST);

        MyGL.checkGLES();
    }
}
