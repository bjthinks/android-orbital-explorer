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
    private int mTextureId;
    private int mFramebufferId;

    public int getTextureId() {
        return mTextureId;
    }

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
        mTextureId = temp[0];

        // Bind it to the TEXTURE_2D attachment point
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        // Set the bound texture's size and format.
        final int textureWidth = 64;
        final int textureHeight = 64;
        // The following three parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (supposedly).
        // TODO check for EXT_color_buffer_float and fall back to internal format RGBA32I
        // if not supported (in which case format = RGBA_INTEGER and type = INT)
        final int textureFormat = GLES30.GL_RGBA;
        final int textureType = GLES30.GL_FLOAT;
        final int textureInternalFormat = GLES30.GL_RGBA32F;
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, textureInternalFormat,
                textureWidth, textureHeight, 0, textureFormat, textureType, null);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        // Generate framebuffer
        GLES30.glGenFramebuffers(1, temp, 0);
        mFramebufferId = temp[0];

        // Bind framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);

        // Attach the texture to the bound framebuffer
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, mTextureId, 0);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
            Log.e(TAG, "Framebuffer not complete");

        // Un-bind framebuffer -- this returns drawing to the default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "demo.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "demo.frag", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        GLES30.glLinkProgram(mProgram);
        getGLError();
    }

    public void render(int width, int height, float[] shaderTransform) {
        GLES30.glViewport(0, 0, 64, 64); // width, height);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);
        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "shaderTransform");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);
        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        getGLError();
    }
}
