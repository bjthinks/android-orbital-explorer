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
    private double mExponentialConstant;
    private FloatBuffer mQuadratureWeights;
    private Texture mRadialTexture;
    private Texture mAzimuthalTexture;
    private Texture mQuadratureWeightTexture;
    private Texture mTexture;
    private Framebuffer mFramebuffer;
    private int mWidth, mHeight;

    private final double MAXIMUM_RADIUS = 16.0;
    private final int RADIAL_TEXTURE_SIZE = 256;
    private final int AZIMUTHAL_TEXTURE_SIZE = 256;
    private final int QUADRATURE_POINTS = 2;
    private final int QUADRATURE_SIZE = 64;

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

        int Z = 8;
        int N = 6;
        int L = 4;
        int M = 1;

        mWaveFunction = new WaveFunction(Z, N, L, M);
        RadialFunction radialFunction = mWaveFunction.getRadialFunction();
        mRadialData = functionToBuffer(radialFunction.oscillatingPart(),
                0.0, MAXIMUM_RADIUS, RADIAL_TEXTURE_SIZE - 1);
        mAzimuthalData = functionToBuffer(mWaveFunction.getAzimuthalFunction(),
                0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);

        // Set up Gaussian Quadrature
        float[] quadratureWeights = new float[2 * QUADRATURE_POINTS * QUADRATURE_SIZE];
        // Multiply by 2 because wave function is squared
        mExponentialConstant = 2.0 * radialFunction.exponentialConstant();
        for (int i = 0; i < QUADRATURE_SIZE; ++i) {
            double distanceFromOrigin = MAXIMUM_RADIUS * (double) i / (double) (QUADRATURE_SIZE - 1);
            WeightFunction weightFunction
                    = new WeightFunction(distanceFromOrigin);
            GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, QUADRATURE_POINTS);
            String logMessage = "Data " + distanceFromOrigin;

            for (int j = 0; j < QUADRATURE_POINTS; ++j) {
                quadratureWeights[2 * QUADRATURE_POINTS * i + 2 * j]
                        = (float) GQ.getNode(j);
                quadratureWeights[2 * QUADRATURE_POINTS * i + 2 * j + 1]
                        = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));

                logMessage += " " + quadratureWeights[2 * QUADRATURE_POINTS * i + 2 * j];
                logMessage += " " + quadratureWeights[2 * QUADRATURE_POINTS * i + 2 * j + 1];
            }

            Log.d(TAG, logMessage);
        }
        mQuadratureWeights = floatArrayToBuffer(quadratureWeights);
    }

    private class WeightFunction implements Function {
        private double mDistanceFromOrigin;

        public WeightFunction(double distanceFromOrigin) {
            mDistanceFromOrigin = distanceFromOrigin;
        }

        public double eval(double x) {
            if (x < 0.0)
                return 0.0;

            double r = Math.sqrt(mDistanceFromOrigin * mDistanceFromOrigin + x * x);
            // Multiply by 2 because the wave function is squared
            double value = Math.exp(mExponentialConstant * r);
            // Multiply by 2 because the wave function is squared
            value *= Math.pow(r, 2.0 * mWaveFunction.getRadialFunction().powerOfR());

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
        mRadialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, mRadialData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create azimuthal texture
        mAzimuthalTexture = new Texture(orbitalFormat, orbitalType, orbitalInternalFormat);
        mAzimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, mAzimuthalData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        final int quadratureFormat = GLES30.GL_RG;
        final int quadratureType = GLES30.GL_FLOAT;
        final int quadratureInternalFormat = GLES30.GL_RG32F;
        // Create quadrature weight texture
        mQuadratureWeightTexture
                = new Texture(quadratureFormat, quadratureType, quadratureInternalFormat);
        mQuadratureWeightTexture.bindToTexture2DAndSetImage(
                QUADRATURE_POINTS, QUADRATURE_SIZE, mQuadratureWeights);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

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
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

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

        setUniformInt("colorMode", mRenderPreferences.getColorMode());
        setUniformInt("numQuadraturePoints", QUADRATURE_POINTS);

        setUniformFloat("exponentialConstant", (float) mExponentialConstant);
        setUniformFloat("maximumRadius", (float) MAXIMUM_RADIUS);
        setUniformFloat("numRadialSubdivisions", (float) (RADIAL_TEXTURE_SIZE - 1));
        setUniformFloat("numAzimuthalSubdivisions", (float) (AZIMUTHAL_TEXTURE_SIZE - 1));
        setUniformFloat("numQuadratureSubdivisions", (float) (QUADRATURE_SIZE - 1));
        setUniformFloat("M", (float) mWaveFunction.getM());
        // Multiply by 2 because the wave function is squared
        setUniformFloat("powerOfR", (float) (2 * mWaveFunction.getRadialFunction().powerOfR()));

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
