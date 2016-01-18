package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;
import java.nio.FloatBuffer;

public class Integrator extends RenderStage {
    private static final String TAG = "Integrator";

    RenderPreferences mRenderPreferences;

    private int mProgram;
    private FloatBuffer mVertexBuffer;

    Orbital orbital;

    private int radialDataSize;
    private Texture mRadialTexture;

    private int azimuthalDataSize;
    private Texture mAzimuthalTexture;

    private Texture mQuadratureWeightTexture;
    private Texture mTexture;
    private Framebuffer mFramebuffer;
    private int mWidth, mHeight;

    private final double MAXIMUM_RADIUS = 16.0;
    private final int QUADRATURE_SIZE = 64;

    public Texture getTexture() {
        return mTexture;
    }

    Integrator(Context context) {
        mRenderPreferences = new RenderPreferences(context);

        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f,
                1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);

        orbital = new Orbital(8, 6, 4, 1);
    }

    public void newContext(AssetManager assetManager) {

        // Create radial texture
        mRadialTexture = new Texture(GLES30.GL_RG, GLES30.GL_FLOAT, GLES30.GL_RG32F);
        float[] radialData = orbital.getRadialData();
        radialDataSize = radialData.length / 2;
        mRadialTexture.bindToTexture2DAndSetImage(radialDataSize, 1, radialData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create azimuthal texture
        mAzimuthalTexture = new Texture(GLES30.GL_RG, GLES30.GL_FLOAT, GLES30.GL_RG32F);
        float[] azimuthalData = orbital.getAzimuthalData();
        azimuthalDataSize = azimuthalData.length / 2;
        mAzimuthalTexture.bindToTexture2DAndSetImage(azimuthalDataSize, 1, azimuthalData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        final int quadratureFormat = GLES30.GL_RGBA;
        final int quadratureType = GLES30.GL_FLOAT;
        final int quadratureInternalFormat = GLES30.GL_RGBA32F;
        // Create quadrature weight texture
        mQuadratureWeightTexture
                = new Texture(quadratureFormat, quadratureType, quadratureInternalFormat);
        mQuadratureWeightTexture.bindToTexture2DAndSetImage(
                orbital.getQuadraturePoints(), QUADRATURE_SIZE, orbital.getQuadratureData());

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create a texture to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).
        mTexture = new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        mTexture.bindToTexture2DAndResize(1, 1);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        mFramebuffer = new Framebuffer();
        mFramebuffer.bindAndSetTexture(mTexture);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "integrator.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "integrator.frag", GLES30.GL_FRAGMENT_SHADER);
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

    public void render(float[] shaderTransform) {

        mFramebuffer.bindToAttachmentPoint();
        GLES30.glViewport(0, 0, mWidth, mHeight);
        GLES30.glUseProgram(mProgram);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        mRadialTexture.bindToTexture2D();
        setUniformInt("radial", 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        mAzimuthalTexture.bindToTexture2D();
        setUniformInt("azimuthal", 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        mQuadratureWeightTexture.bindToTexture2D();
        setUniformInt("quadrature", 2);

        setUniformInt("colorMode", mRenderPreferences.getColorMode());
        setUniformInt("numQuadraturePoints", orbital.getQuadraturePoints());

        setUniformFloat("exponentialConstant", (float) (2.0 * orbital.getRadialExponent()));
        setUniformFloat("maximumRadius", (float) MAXIMUM_RADIUS);
        setUniformFloat("numRadialSubdivisions", (float) (radialDataSize - 1));
        setUniformFloat("numAzimuthalSubdivisions", (float) (azimuthalDataSize - 1));
        setUniformFloat("numQuadratureSubdivisions", (float) (QUADRATURE_SIZE - 1));
        setUniformFloat("M", (float) orbital.getM());
        // Multiply by 2 because the wave function is squared
        setUniformFloat("powerOfR", (float) (2 * orbital.getRadialPower()));

        // For testing
        setUniformFloat("zero", 0.0f);
        setUniformFloat("one", 1.0f);

        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "shaderTransform");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);

        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);

        final int zeroes[] = {0, 0, 0, 0};
        GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

    void setUniformInt(String name, int value) {
        int handle = GLES30.glGetUniformLocation(mProgram, name);
        GLES30.glUniform1i(handle, value);
    }

    void setUniformFloat(String name, float value) {
        int handle = GLES30.glGetUniformLocation(mProgram, name);
        GLES30.glUniform1f(handle, value);
    }
}
