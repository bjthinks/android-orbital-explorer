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
    private final int RADIAL_TEXTURE_SIZE = 128;
    private final int AZIMUTHAL_TEXTURE_SIZE = 64;
    private final int QUADRATURE_POINTS = 2;
    private final int QUADRATURE_SIZE = 16;

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
        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "integrate.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "integrate.frag", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        try {
            GLES30.glLinkProgram(mProgram);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        getGLError();
    }

    public void resize(int width, int height) {
    }

    public void render(float[] shaderTransform) {
    }
}
