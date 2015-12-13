package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;
import java.nio.FloatBuffer;

public class Integrate extends RenderStage {
    private static final String TAG = "Integrate";

    RenderPreferences mRenderPreferences;

    private int mProgram;
    private FloatBuffer mVertexBuffer;
    private WaveFunction mWaveFunction;
    private FloatBuffer mRadialData;
    private FloatBuffer mAzimuthalData;
    private FloatBuffer mQuadratureWeights;
    private FloatBuffer mQuadratureWeights2;
    private Texture mRadialTexture;
    private Texture mAzimuthalTexture;
    private Texture mQuadratureWeightTexture;
    private Texture mQuadratureWeightTexture2;
    private Texture mTexture;
    private Framebuffer mFramebuffer;
    private int mWidth, mHeight;

    public Texture getTexture() {
        return mTexture;
    }

    Integrate(Context context) {
        mRenderPreferences = new RenderPreferences(context);

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
        RadialFunction radialFunction = mWaveFunction.getRadialFunction();
        mRadialData = functionToBuffer(radialFunction.nonExponentialPart(), 0.0, 16.0, 1024);
        mAzimuthalData = functionToBuffer(mWaveFunction.getAzimuthalFunction(),
                0.0, Math.PI, 1024);

        // Set up Gaussian Quadrature
        float[] quadratureWeights = new float[4 * 65];
        float[] quadratureWeights2 = new float[4 * 65];
        // Multiply by 2 because wave function is squared
        double exponentialConstant = 2.0 * radialFunction.exponentialConstant();
        for (int i = 0; i <= 64; ++i) {
            double distanceFromOrigin = 16.0 * (double) i / 64.0;
            WeightFunction weightFunction
                    = new WeightFunction(exponentialConstant, distanceFromOrigin);
            GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, 4);
            quadratureWeights[4 * i]     = (float) GQ.getNode(0);
            quadratureWeights[4 * i + 1] = (float) GQ.getWeight(0);
            quadratureWeights[4 * i + 2] = (float) GQ.getNode(1);
            quadratureWeights[4 * i + 3] = (float) GQ.getWeight(1);
            quadratureWeights2[4 * i]     = (float) GQ.getNode(2);
            quadratureWeights2[4 * i + 1] = (float) GQ.getWeight(2);
            quadratureWeights2[4 * i + 2] = (float) GQ.getNode(3);
            quadratureWeights2[4 * i + 3] = (float) GQ.getWeight(3);
            if (i % 8 == 0)
                Log.d(TAG, "Data " + i + " :"
                        + " " + quadratureWeights[4 * i]
                        + " " + quadratureWeights[4 * i + 1]
                        + " " + quadratureWeights[4 * i + 2]
                        + " " + quadratureWeights[4 * i + 3]
                        + " " + quadratureWeights2[4 * i]
                        + " " + quadratureWeights2[4 * i + 1]
                        + " " + quadratureWeights2[4 * i + 2]
                        + " " + quadratureWeights2[4 * i + 3]);
        }
        mQuadratureWeights = floatArrayToBuffer(quadratureWeights);
        mQuadratureWeights2 = floatArrayToBuffer(quadratureWeights2);
    }

    private class WeightFunction implements Function {
        private double mExponentialConstant;
        private double mDistanceFromOrigin;

        public WeightFunction(double exponentialConstant, double distanceFromOrigin) {
            mExponentialConstant = exponentialConstant;
            mDistanceFromOrigin = distanceFromOrigin;
        }

        public double eval(double x) {
            if (x < 0.0)
                return 0.0;

            double r = Math.sqrt(mDistanceFromOrigin * mDistanceFromOrigin + x * x);
            // Multiply by 2 because the wave function is squared
            double value = Math.exp(mExponentialConstant * r);

            if (x == 0.0)
                value *= 0.5;

            return value;
        }
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

        final int quadratureFormat = GLES30.GL_RGBA;
        final int quadratureType = GLES30.GL_FLOAT;
        final int quadratureInternalFormat = GLES30.GL_RGBA32F;
        // Create quadrature weight texture
        mQuadratureWeightTexture
                = new Texture(quadratureFormat, quadratureType, quadratureInternalFormat);
        mQuadratureWeightTexture.bindToTexture2DAndSetImage(64 + 1, 1, mQuadratureWeights);

        // Floating point textures are not filterable
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        mQuadratureWeightTexture2
                = new Texture(quadratureFormat, quadratureType, quadratureInternalFormat);
        mQuadratureWeightTexture2.bindToTexture2DAndSetImage(64 + 1, 1, mQuadratureWeights2);

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
        mFramebuffer.bindAndSetTexture(mTexture);

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

        GLES30.glActiveTexture(GLES30.GL_TEXTURE3);
        mQuadratureWeightTexture2.bindToTexture2D();
        setUniformInt("quadrature2", 3);

        setUniformInt("colorMode", mRenderPreferences.getColorMode());

        setUniformFloat("maximumRadius", 16.0f);
        setUniformFloat("numRadialSubdivisions", 1024.0f);
        setUniformFloat("numAzimuthalSubdivisions", 1024.0f);
        setUniformFloat("numQuadratureSubdivisions", 64.0f);
        setUniformFloat("M", (float) mWaveFunction.getM());

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
