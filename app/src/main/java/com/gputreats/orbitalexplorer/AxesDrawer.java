package com.gputreats.orbitalexplorer;

import static java.lang.Math.max;
import static java.lang.Math.round;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.DisplayMetrics;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class AxesDrawer {

    final FloatBuffer axes, axisRect, colors, arrows;
    final ByteBuffer axisBuffer, originBuffer, arrowBuffer;
    private final int arrowSize = 64, originSize = 32;
    private int axisTexture, originTexture, arrowTexture;
    private final AssetManager assets;
    private final AppPreferences appPreferences;
    private Program axesProgram, axisRectProgram, originProgram, arrowProgram;
    private float lineWidth;
    private final int axisWidth;

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

        float[] axisRectangle = {
                0.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,
                0.0f,  1.0f, 0.0f
        };
        axisRect = FloatBufferFactory.make(axisRectangle);

        float[] arrowCoordinates = { // Note this is both coordinates and colors :)
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f
        };
        arrows = FloatBufferFactory.make(arrowCoordinates);

        appPreferences = new AppPreferences(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        lineWidth = max(round(((float) metrics.densityDpi) / 64.0f), 1.0f);
        assets = context.getAssets();

        axisWidth = 2 + (int) lineWidth;
        byte[] axisData = new byte[axisWidth];
        for (int i = 1; i < axisWidth - 1; ++i)
            axisData[i] = -1;
        axisBuffer = ByteBuffer.allocateDirect(axisWidth);
        axisBuffer.put(axisData);
        axisBuffer.position(0);

        byte[] originData = (new ReadBytes(assets, "textures/origin.raw",
                originSize * originSize)).get();
        originBuffer = ByteBuffer.allocateDirect(originSize * originSize);
        originBuffer.put(originData);
        originBuffer.position(0);

        byte[] arrowData = (new ReadBytes(assets, "textures/arrow.raw",
                arrowSize * arrowSize)).get();
        arrowBuffer = ByteBuffer.allocateDirect(arrowSize * arrowSize);
        arrowBuffer.put(arrowData);
        arrowBuffer.position(0);
    }

    public void onSurfaceCreated() {
        MyGL.checkGLES();

        axesProgram = new Program(assets,
                "axes.vert", "axes.frag");
        axisRectProgram = new Program(assets,
                "axisRect.vert", "axisRect.frag");
        originProgram = new Program(assets,
                "origin.vert", "origin.frag");
        arrowProgram = new Program(assets,
                "arrow.vert", "arrow.frag");
        MyGL.checkGLES();

        int[] temp = new int[3];
        GLES30.glGenTextures(3, temp, 0);
        axisTexture = temp[0];
        originTexture = temp[1];
        arrowTexture = temp[2];

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, axisTexture);
        // This needs to be axisWidth by 1, not vice versa, because of unpack alignment crud
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_R8, axisWidth, 1,
                0, GLES30.GL_RED, GLES30.GL_UNSIGNED_BYTE, axisBuffer);
        // R8 textures are linearly filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // No mipmaps for this texture
        MyGL.checkGLES();

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, originTexture);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_R8, originSize, originSize,
                0, GLES30.GL_RED, GLES30.GL_UNSIGNED_BYTE, originBuffer);
        // R8 textures are linearly filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        MyGL.checkGLES();

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, arrowTexture);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_R8, arrowSize, arrowSize,
                0, GLES30.GL_RED, GLES30.GL_UNSIGNED_BYTE, arrowBuffer);
        // R8 textures are linearly filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        MyGL.checkGLES();

        float[] lineWidthRange = new float[2];
        GLES30.glGetFloatv(GLES30.GL_ALIASED_LINE_WIDTH_RANGE, lineWidthRange, 0);
        if (lineWidthRange[1] < lineWidth) {
            lineWidth = (float) Math.floor(lineWidthRange[1]);
            if (lineWidth < 1.0f)
                lineWidth = 1.0f;
        }
        MyGL.checkGLES();
    }

    private int width, height;

    public void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public void render(OrbitalData orbitalData, float[] transform) {
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
        GLES30.glDrawArrays(GLES30.GL_LINES, 2, 4);
        GLES30.glDisableVertexAttribArray(axisPositionHandle);
        GLES30.glDisableVertexAttribArray(axisColorHandle);

        axisRectProgram.use();

        int axisRectPositionHandle = axisRectProgram.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(axisRectPositionHandle);
        GLES30.glVertexAttribPointer(axisRectPositionHandle, 3, GLES30.GL_FLOAT, false,
                12, axisRect);

        float ah = 20.0f * (float) axisWidth / (float) height;
        float[] axisRectMatrix = {
                1f, 0f, 0f, 0f,
                0f, ah, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        };
        int axisRectMatrixHandle = axisRectProgram.getUniformLocation("axisMatrix");
        GLES30.glUniformMatrix4fv(axisRectMatrixHandle, 1, false, axisRectMatrix, 0);

        int axisRectProjectionMatrixHandle = axisRectProgram.getUniformLocation("projectionMatrix");
        GLES30.glUniformMatrix4fv(axisRectProjectionMatrixHandle, 1, false, transform, 0);

        int axisRectScalingMatrixHandle = axisRectProgram.getUniformLocation("scalingMatrix");
        GLES30.glUniformMatrix4fv(axisRectScalingMatrixHandle, 1, false, scalingMatrix, 0);

        int axisRectColorHandle = axisRectProgram.getUniformLocation("color");
        GLES30.glUniform3f(axisRectColorHandle, 1.0f, 0.0f, 0.0f);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, axisTexture);
        axisRectProgram.setUniform1i("axis", 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(axisRectPositionHandle);

        MyGL.checkGLES();

        originProgram.use();
        originProgram.setUniform1f("originSize", 2.0f * lineWidth);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, originTexture);
        originProgram.setUniform1i("origin", 0);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1);

        MyGL.checkGLES();

        arrowProgram.use();

        int arrowPositionHandle = arrowProgram.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(arrowPositionHandle);
        GLES30.glVertexAttribPointer(arrowPositionHandle, 3, GLES30.GL_FLOAT, false,
                12, arrows);

        int arrowProjectionMatrixHandle = arrowProgram.getUniformLocation("projectionMatrix");
        GLES30.glUniformMatrix4fv(arrowProjectionMatrixHandle, 1, false, transform, 0);

        int arrowScalingMatrixHandle = arrowProgram.getUniformLocation("scalingMatrix");
        GLES30.glUniformMatrix4fv(arrowScalingMatrixHandle, 1, false, scalingMatrix, 0);

        arrowProgram.setUniform1f("arrowSize", 6.0f * lineWidth);

        int screenDimensionsHandle = arrowProgram.getUniformLocation("screenDimensions");
        GLES30.glUniform2f(screenDimensionsHandle, width, height);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, arrowTexture);
        arrowProgram.setUniform1i("arrow", 0);

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
