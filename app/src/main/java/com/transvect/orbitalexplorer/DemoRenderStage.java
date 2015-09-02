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

        // Set the bound texture's size and format.
        final int textureWidth = 64;
        final int textureHeight = 64;
        // The following three parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error.
        // For the expensive 32-bit floating point textures that this app requires,
        // the relevant rows of Table 3.2 are:
        // format  type   sized internalformat
        // RED     FLOAT  R32F
        // RG      FLOAT  RG32F
        // RGB     FLOAT  RGB32F
        // RGBA    FLOAT  RGBA32F
        // TODO: try RGB16F, R11F_G11F_B11F, & RGB9_E5 and see if they give good results.
        final int textureFormat = GLES30.GL_RGB;
        final int textureType = GLES30.GL_FLOAT;
        final int textureInternalFormat = GLES30.GL_RGB32F;
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, textureInternalFormat,
                textureWidth, textureHeight, 0, textureFormat, textureType, null);

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
