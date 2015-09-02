package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;
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
        GLES30.glGenTextures(1, temp, 0);
        int textureId = temp[0];
        Log.d(TAG, "Texture id = " + textureId);

        // Bind it to the TEXTURE_2D attachment point
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        final int textureWidth = 64;
        final int textureHeight = 64;
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB, textureWidth, textureHeight, 0, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, null);

        // Generate framebuffer
        GLES30.glGenFramebuffers(1, temp, 0);
        int framebufferId = temp[0];
        Log.d(TAG, "Framebuffer id = " + framebufferId);

        // Bind framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, framebufferId);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != 0)
            Log.e(TAG, "Framebuffer not complete, code = " + status);

        // Un-bind framebuffer -- this returns drawing to the default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "vertex.glsl", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "fragment.glsl", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        GLES30.glLinkProgram(mProgram);
        getGLError();
    }

    public void render(float[] shaderTransform) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);
        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "shaderTransform");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);
        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

}
