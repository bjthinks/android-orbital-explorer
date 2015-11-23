package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;
import java.nio.FloatBuffer;

public class Integrate extends RenderStage {
    private static final String TAG = "Integrate";

    private int mProgram;
    private FloatBuffer mVertexBuffer;
    private WaveFunction mWaveFunction;
    private FloatBuffer mRadialData;
    private FloatBuffer mAzimuthalData;
    private Texture mRadialTexture;
    private Texture mAzimuthalTexture;
    private Texture mTexture;
    private Framebuffer mFramebuffer;
    private int mWidth, mHeight;

    public Texture getTexture() {
        return mTexture;
    }

    Integrate() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f,
                1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);

        int Z = 4;
        int N = 4;
        int L = 2;
        int M = 1;

        mWaveFunction = new WaveFunction(Z, N, L, M);
        mRadialData = functionToBuffer(mWaveFunction.getRadialFunction(),
                0.0, 16.0, 1024);
        mAzimuthalData = functionToBuffer(mWaveFunction.getAzimuthalFunction(),
                0.0, Math.PI, 1024);
    }

    public void newContext(AssetManager assetManager) {

        // Create input textures with orbital data

        // Data format
        final int orbitalFormat = GLES30.GL_RED;
        final int orbitalType = GLES30.GL_FLOAT;
        final int orbitalInternalFormat = GLES30.GL_R32F;

        // Create radial texture
        mRadialTexture = new Texture(orbitalFormat, orbitalType, orbitalInternalFormat);
        mRadialTexture.bindToTexture2DAndSetImage(1024 + 1, 1, mRadialData);

        // Floating point textures are not filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        // Create azimuthal texture
        mAzimuthalTexture = new Texture(orbitalFormat, orbitalType, orbitalInternalFormat);
        mAzimuthalTexture.bindToTexture2DAndSetImage(1024 + 1, 1, mAzimuthalData);

        // Floating point textures are not filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        // The following three parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).
        final int renderFormat = GLES30.GL_RGBA_INTEGER;
        final int renderType = GLES30.GL_INT;
        final int renderInternalFormat = GLES30.GL_RGBA32I;

        // Create a texture to render to
        mTexture = new Texture(renderFormat, renderType, renderInternalFormat);
        mTexture.bindToTexture2DAndResize(1, 1);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        mFramebuffer = new Framebuffer();
        mFramebuffer.bindToAttachmentPoint();

        // Attach the texture to the bound framebuffer
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, mTexture.getId(), 0);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
            Log.e(TAG, "Framebuffer not complete");

        // Un-bind framebuffer -- this returns drawing to the default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "integrate.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "integrate.frag", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        GLES30.glLinkProgram(mProgram);
        getGLError();
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mTexture.bindToTexture2DAndResize(mWidth, mHeight);
    }

    private static final int zeroes[] = {0, 0, 0, 0};
    public void render(float[] shaderTransform) {
        mFramebuffer.bindToAttachmentPoint();
        GLES30.glViewport(0, 0, mWidth, mHeight);
        GLES30.glUseProgram(mProgram);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        mRadialTexture.bindToTexture2D();
        int radialHandle = GLES30.glGetUniformLocation(mProgram, "radial");
        GLES30.glUniform1i(radialHandle, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        mAzimuthalTexture.bindToTexture2D();
        int azimuthalHandle = GLES30.glGetUniformLocation(mProgram, "azimuthal");
        GLES30.glUniform1i(azimuthalHandle, 1);

        int MHandle = GLES30.glGetUniformLocation(mProgram, "M");
        GLES30.glUniform1f(MHandle, (float) mWaveFunction.getM());

        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "shaderTransform");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);

        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);

        GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }
}
