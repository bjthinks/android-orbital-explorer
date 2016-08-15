package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.os.Handler;
import android.os.Message;

import java.nio.ByteBuffer;

class ScreenDrawer extends RenderStage {

    private AssetManager assets;

    private Program programColor, programMono;

    ScreenDrawer(Context context) {
        assets = context.getAssets();
    }

    void onSurfaceCreated() throws OpenGLException {
        MyGL.checkGLES();
        programColor = new Program(assets, "6", "5");
        programMono = new Program(assets, "8", "7");
    }

    private int inputWidth, inputHeight;
    private int width, height;

    void resize(int newInputWidth, int newInputHeight, int newWidth, int newHeight) {
        inputWidth = newInputWidth;
        inputHeight = newInputHeight;
        width = newWidth;
        height = newHeight;
    }

    void render(Texture texture, int N, boolean color, Handler screenGrabHandler)
            throws OpenGLException {

        MyGL.checkGLES();

        Program program = color ? programColor : programMono;

        GLES30.glViewport(0, 0, width, height);
        program.use();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        texture.bindToTexture2D();
        program.setUniform1i("data", 0);

        GLES30.glUniform2f(program.getUniformLocation("texSize"),
                (float) inputWidth, (float) inputHeight);

        GLES30.glUniform2i(program.getUniformLocation("upperClamp"),
                inputWidth - 1, inputHeight - 1);

        float[] rot = new float[4];
        long period = N * N * 1000; // ms
        double t = 2. * Math.PI * (double) (System.currentTimeMillis() % period) / (double) period;
        rot[0] = (float) Math.cos(t);  rot[2] = (float) -Math.sin(t);
        rot[1] = (float) Math.sin(t);  rot[3] = (float) Math.cos(t);
        GLES30.glUniformMatrix2fv(program.getUniformLocation("colorRotation"), 1, false, rot, 0);

        int inPositionHandle = program.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                screenRectangle);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);

        if (screenGrabHandler != null) {
            ByteBuffer buf = ByteBuffer.allocate(width * height * 4);
            GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
            Message.obtain(screenGrabHandler, 0, width, height, buf).sendToTarget();
        }

        MyGL.checkGLES();
    }
}
