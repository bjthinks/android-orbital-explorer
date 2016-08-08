package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.os.Message;

import java.nio.ByteBuffer;

public class ScreenDrawer extends RenderStage {

    AssetManager assetManager;

    private Program programColor, programMono;

    public ScreenDrawer(Context context) {
        assetManager = context.getAssets();
    }

    public void onSurfaceCreated() throws OpenGLException {
        MyGL.checkGLES();
        programColor = new Program(assetManager, "6", "5");
        programMono = new Program(assetManager, "8", "7");
    }

    private int inputWidth, inputHeight;
    private int width, height;

    public void resize(int newInputWidth, int newInputHeight, int newWidth, int newHeight) {
        inputWidth = newInputWidth;
        inputHeight = newInputHeight;
        width = newWidth;
        height = newHeight;
    }

    public void render(Texture texture, RenderState.FrozenState frozenState) throws OpenGLException {

        MyGL.checkGLES();

        boolean color = frozenState.orbital.color;
        Program program = color ? programColor : programMono;

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glViewport(0, 0, width, height);
        program.use();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        texture.bindToTexture2D();
        program.setUniform1i("data", 0);

        int texSizeHandle = program.getUniformLocation("texSize");
        GLES30.glUniform2f(texSizeHandle, (float) inputWidth, (float) inputHeight);

        int upperClampHandle = program.getUniformLocation("upperClamp");
        GLES30.glUniform2i(upperClampHandle, inputWidth - 1, inputHeight - 1);

        int colorRotation = program.getUniformLocation("colorRotation");
        float[] rot = new float[4];
        int N = frozenState.orbital.N;
        long period = N * N * 1000; // ms
        double t = 2. * Math.PI * (double) (System.currentTimeMillis() % period) / (double) period;
        rot[0] = (float) Math.cos(t);  rot[2] = (float) -Math.sin(t);
        rot[1] = (float) Math.sin(t);  rot[3] = (float) Math.cos(t);
        GLES30.glUniformMatrix2fv(colorRotation, 1, false, rot, 0);

        int inPositionHandle = program.getAttribLocation("inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                screenRectangle);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);

        if (frozenState.screenGrabRequested) {
            ByteBuffer buf = ByteBuffer.allocate(width * height * 4);
            GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
            Message.obtain(frozenState.screenGrabHandler, 0, width, height, buf).sendToTarget();
        }

        MyGL.checkGLES();
    }
}
