package com.gputreats.orbitalexplorer;

import static java.lang.Math.max;
import static java.lang.Math.round;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.DisplayMetrics;
import java.nio.FloatBuffer;

public class AxesDrawer {

    final FloatBuffer axes, colors, arrows;
    final byte[] arrowData;
    private final AssetManager assets;
    private final AppPreferences appPreferences;
    private Program axesProgram, originProgram, arrowProgram;
    private final float lineWidth;

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

        float[] arrowCoordinates = {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f
        };
        // Note this is both coordinates and colors :)
        arrows = FloatBufferFactory.make(arrowCoordinates);

        assets = context.getAssets();
        arrowData = (new ReadBytes(assets, "textures/arrow.raw", 64 * 64)).get();

        appPreferences = new AppPreferences(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        lineWidth = max(round(((float) metrics.densityDpi) / 64.0f), 1.0f);
    }

    public void onSurfaceCreated() {
        MyGL.checkGLES();
        axesProgram = new Program(assets, "axes.vert", "axes.frag");
        originProgram = new Program(assets, "origin.vert", "origin.frag");
        arrowProgram = new Program(assets, "arrow.vert", "arrow.frag");
        MyGL.checkGLES();
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

        boolean savedDepthTest = GLES30.glIsEnabled(GLES30.GL_DEPTH_TEST);
        boolean savedScissorTest = GLES30.glIsEnabled(GLES30.GL_SCISSOR_TEST);
        boolean savedBlend = GLES30.glIsEnabled(GLES30.GL_BLEND);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_SCISSOR_TEST);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendEquation(GLES30.GL_MAX);
        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE);
        GLES30.glViewport(0, 0, width, height);

        MyGL.checkGLES();

        axesProgram.use();

        int axisProjectionMatrixHandle = axesProgram.getUniformLocation("projectionMatrix");
        GLES30.glUniformMatrix4fv(axisProjectionMatrixHandle, 1, false, transform, 0);

        float mr = (float) orbitalData.getOrbital().getRadialFunction().getMaximumRadius();
        mr *= 0.75f;
        float[] scalingMatrix = {
                mr, 0f, 0f, 0f,
                0f, mr, 0f, 0f,
                0f, 0f, mr, 0f,
                0f, 0f, 0f, 1f
        };
        int axisScalingMatrixHandle = axesProgram.getUniformLocation("scalingMatrix");
        GLES30.glUniformMatrix4fv(axisScalingMatrixHandle, 1, false, scalingMatrix, 0);

        MyGL.checkGLES();

        int axisPositionHandle = axesProgram.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(axisPositionHandle);
        GLES30.glVertexAttribPointer(axisPositionHandle, 3, GLES30.GL_FLOAT, false,
                12, axes);

        int axisColorHandle = axesProgram.getAttribLocation("inColor");
        GLES30.glEnableVertexAttribArray(axisColorHandle);
        GLES30.glVertexAttribPointer(axisColorHandle, 3, GLES30.GL_FLOAT, false,
                12, colors);

        MyGL.checkGLES();

        GLES30.glLineWidth(lineWidth);
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, 6);
        GLES30.glDisableVertexAttribArray(axisPositionHandle);
        GLES30.glDisableVertexAttribArray(axisColorHandle);

        originProgram.use();
        originProgram.setUniform1f("originSize", 2.0f * lineWidth);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1);

        arrowProgram.use();

        int arrowPositionHandle = arrowProgram.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(arrowPositionHandle);
        GLES30.glVertexAttribPointer(arrowPositionHandle, 3, GLES30.GL_FLOAT, false,
                12, arrows);

        int arrowProjectionMatrixHandle = arrowProgram.getUniformLocation("projectionMatrix");
        GLES30.glUniformMatrix4fv(arrowProjectionMatrixHandle, 1, false, transform, 0);

        int arrowScalingMatrixHandle = arrowProgram.getUniformLocation("scalingMatrix");
        GLES30.glUniformMatrix4fv(arrowScalingMatrixHandle, 1, false, scalingMatrix, 0);

        arrowProgram.setUniform1f("arrowSize", 5.0f * lineWidth);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 3);
        GLES30.glDisableVertexAttribArray(arrowPositionHandle);

        if (savedDepthTest)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        if (savedScissorTest)
            GLES30.glEnable(GLES30.GL_SCISSOR_TEST);
        if (!savedBlend)
            GLES30.glDisable(GLES30.GL_BLEND);

        MyGL.checkGLES();
    }
}
