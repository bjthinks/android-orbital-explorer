package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by bwj on 8/31/15.
 */
public class DemoRenderStage extends RenderStage {
    private static final String TAG = "DemoRenderStage";

    private int mProgram;
    private FloatBuffer mVertexBuffer;

    DemoRenderStage() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f,
                1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);
    }

    public void newContext(AssetManager assetManager) {

        int temp[] = new int[1];

        // Generate output texture
        GLES20.glGenTextures(1, temp, 0);
        int textureId = temp[0];
        Log.d(TAG, "Texture id = " + textureId);

        // Bind it to the TEXTURE_2D attachment point
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        final int textureWidth = 64;
        final int textureHeight = 64;
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, textureWidth, textureHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);

        // Generate framebuffer
        GLES20.glGenFramebuffers(1, temp, 0);
        int framebufferId = temp[0];
        Log.d(TAG, "Framebuffer id = " + framebufferId);

        // Bind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);

        // Check if framebuffer is complete
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != 0)
            Log.e(TAG, "Framebuffer not complete, code = " + status);

        // Un-bind framebuffer -- this returns drawing to the default framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "vertex.glsl", GLES20.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "fragment.glsl", GLES20.GL_FRAGMENT_SHADER);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader.getId());
        GLES20.glAttachShader(mProgram, fragmentShader.getId());
        GLES20.glLinkProgram(mProgram);
        getGLError();
    }

    public void render(float[] shaderTransform) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "shaderTransform");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);
        int inPositionHandle = GLES20.glGetAttribLocation(mProgram, "inPosition");
        GLES20.glEnableVertexAttribArray(inPositionHandle);
        GLES20.glVertexAttribPointer(inPositionHandle, 2, GLES20.GL_FLOAT, false, 8, mVertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

}
