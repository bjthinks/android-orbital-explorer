package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

public class ScreenDrawer extends RenderStage {

    private int program;

    public ScreenDrawer() {}

    public void newContext(AssetManager assetManager) {
        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "screendrawer.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "screendrawer.frag", GLES30.GL_FRAGMENT_SHADER);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader.getId());
        GLES30.glAttachShader(program, fragmentShader.getId());
        GLES30.glLinkProgram(program);
        getGLError();
    }

    private int inputWidth, inputHeight;
    private int width, height;

    public void resize(int newInputWidth, int newInputHeight, int newWidth, int newHeight) {
        inputWidth = newInputWidth;
        inputHeight = newInputHeight;
        width = newWidth;
        height = newHeight;
    }

    public void render(Texture texture) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(program);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        texture.bindToTexture2D();
        int dataHandle = GLES30.glGetUniformLocation(program, "data");
        GLES30.glUniform1i(dataHandle, 0);

        int texSizeHandle = GLES30.glGetUniformLocation(program, "texSize");
        GLES30.glUniform2f(texSizeHandle, (float) inputWidth, (float) inputHeight);

        int colorRotation = GLES30.glGetUniformLocation(program, "colorRotation");
        float[] rot = new float[4];
        int period = 10000; // ms
        double t = 2 * Math.PI * (double) (System.currentTimeMillis() % period) / period;
        rot[0] = (float) Math.cos(t);  rot[2] = (float) -Math.sin(t);
        rot[1] = (float) Math.sin(t);  rot[3] = (float) Math.cos(t);
        GLES30.glUniformMatrix2fv(colorRotation, 1, false, rot, 0);

        int inPositionHandle = GLES30.glGetAttribLocation(program, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                screenRectangle);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }
}
