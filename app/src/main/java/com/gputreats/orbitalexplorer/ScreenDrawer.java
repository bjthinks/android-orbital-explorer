package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

public class ScreenDrawer extends RenderStage {

    AssetManager assetManager;

    private int programColor, programMono;

    public ScreenDrawer(Context context) {
        assetManager = context.getAssets();
    }

    public void newContext() {
        // Compile & link GLSL programs
        Shader vertexShaderColor = new Shader(assetManager, "6", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShaderColor = new Shader(assetManager, "5", GLES30.GL_FRAGMENT_SHADER);
        programColor = GLES30.glCreateProgram();
        GLES30.glAttachShader(programColor, vertexShaderColor.getId());
        GLES30.glAttachShader(programColor, fragmentShaderColor.getId());
        GLES30.glLinkProgram(programColor);
        getGLError();

        Shader vertexShaderMono = new Shader(assetManager, "8", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShaderMono = new Shader(assetManager, "7", GLES30.GL_FRAGMENT_SHADER);
        programMono = GLES30.glCreateProgram();
        GLES30.glAttachShader(programMono, vertexShaderMono.getId());
        GLES30.glAttachShader(programMono, fragmentShaderMono.getId());
        GLES30.glLinkProgram(programMono);
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

    boolean color;
    public void render(Texture texture, RenderState.FrozenState frozenState) {
        color = frozenState.color;

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glViewport(0, 0, width, height);
        if (color)
            GLES30.glUseProgram(programColor);
        else
            GLES30.glUseProgram(programMono);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        texture.bindToTexture2D();
        int dataHandle = getUniformHandle("data");
        GLES30.glUniform1i(dataHandle, 0);

        int texSizeHandle = getUniformHandle("texSize");
        GLES30.glUniform2f(texSizeHandle, (float) inputWidth, (float) inputHeight);

        int upperClampHandle = getUniformHandle("upperClamp");
        GLES30.glUniform2i(upperClampHandle, inputWidth - 1, inputHeight - 1);

        int colorRotation = getUniformHandle("colorRotation");
        float[] rot = new float[4];
        int N = frozenState.orbital.N;
        long period = N * N * 1000; // ms
        double t = 2. * Math.PI * (double) (System.currentTimeMillis() % period) / (double) period;
        rot[0] = (float) Math.cos(t);  rot[2] = (float) -Math.sin(t);
        rot[1] = (float) Math.sin(t);  rot[3] = (float) Math.cos(t);
        GLES30.glUniformMatrix2fv(colorRotation, 1, false, rot, 0);

        int inPositionHandle;
        if (color)
            inPositionHandle = GLES30.glGetAttribLocation(programColor, "inPosition");
        else
            inPositionHandle = GLES30.glGetAttribLocation(programMono, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                screenRectangle);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

    int getUniformHandle(String name) {
        int handle;
        if (color)
            handle = GLES30.glGetUniformLocation(programColor, name);
        else
            handle = GLES30.glGetUniformLocation(programMono, name);
        return handle;
    }

    void setUniformInt(String name, int value) {
        GLES30.glUniform1i(getUniformHandle(name), value);
    }

    void setUniformFloat(String name, float value) {
        GLES30.glUniform1f(getUniformHandle(name), value);
    }
}
