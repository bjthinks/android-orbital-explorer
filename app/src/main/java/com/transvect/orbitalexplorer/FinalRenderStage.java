package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

/**
 * Created by bwj on 9/1/15.
 */
public class FinalRenderStage extends RenderStage {

    private FloatBuffer mVertexBuffer;
    private int mProgram;

    FinalRenderStage() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f,
                 1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);
    }

    public void newContext(AssetManager assetManager) {
        Shader vertexShader = new Shader(assetManager, "final.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "final.frag", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        GLES30.glLinkProgram(mProgram);
        getGLError();
    }

    public void render(int width, int height, int textureId) {
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(mProgram);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        int dataHandle = GLES30.glGetUniformLocation(mProgram, "data");
        GLES30.glUniform1i(dataHandle, 0);
        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }
}
