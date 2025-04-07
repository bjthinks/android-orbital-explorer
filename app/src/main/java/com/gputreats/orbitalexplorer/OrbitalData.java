package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;

class OrbitalData {

    private final AssetManager assets;

    private Orbital orbital;

    private Texture azimuthalTexture;
    private Texture quadratureTexture;
    private Texture radialTexture;

    private boolean bReal;
    private float fBrightness;
    private float fInverseAzimuthalStepSize;
    private float fInverseQuadratureStepSize;
    private float fInverseRadialStepSize;
    private float fM;
    private float fRadialExponent;
    private float fRadialPower;
    private int iAzimuthalSteps;
    private int iOrder;
    private int iQuadratureSteps;
    private int iRadialSteps;

    OrbitalData(Context context) {
        assets = context.getAssets();
    }

    void onSurfaceCreated() {
        radialTexture     = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        azimuthalTexture  = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);

        MyGL.checkGLES();
    }

    Orbital getOrbital() {
        return orbital;
    }

    void loadOrbital(Orbital newOrbital) {
        if (newOrbital.notEquals(orbital)) {
            orbital = newOrbital;

            RadialFunction radialFunction = orbital.getRadialFunction();
            AzimuthalFunction azimuthalFunction = orbital.getAzimuthalFunction();
            Quadrature quadrature = orbital.getQuadrature();

            // Load new quadrature texture
            int order = quadrature.getOrder();
            float[] quadratureData = new QuadratureData(assets, quadrature).get();
            int quadratureSteps = quadrature.getSteps();
            quadratureTexture.bindToTexture2DAndSetImage(order, quadratureSteps, quadratureData);

            // Calculate radius info
            float quadratureRadius = (float) radialFunction.getMaximumRadius();
            float maxLateral = quadratureData[quadratureData.length - 2];
            float maximumRadius = (float) Math.sqrt((double) (quadratureRadius * quadratureRadius
                    + maxLateral * maxLateral));

            // Load new radial texture
            final int radialTextureSize = 1024;
            float[] radialData = functionToBuffer2(radialFunction.getOscillatingPart(),
                    0.0, (double) maximumRadius, radialTextureSize);
            if (BuildConfig.DEBUG) {
                Log.d("Rad", "Exponential constant = " +
                        radialFunction.getExponentialConstant());
                Log.d("Rad", "Power of r = " +
                        radialFunction.getPowerOfR());
                int m = 0;
                for (int i = 0; i < 1024; ++i)
                    if (radialData[i] > radialData[m])
                        m = i;
                Log.d("Rad", "Radial texture maximum value = " + m + " " + radialData[m]);
                Log.d("Rad", "Maximum radius = " +
                        radialFunction.getMaximumRadius());
            }
            radialTexture.bindToTexture2DAndSetImage(radialTextureSize, 1, radialData);

            // Load new azimuthal texture
            final int azimuthalTextureSize = 256;
            float[] azimuthalData = functionToBuffer2(azimuthalFunction,
                    0.0, Math.PI, azimuthalTextureSize);
            azimuthalTexture.bindToTexture2DAndSetImage(azimuthalTextureSize, 1, azimuthalData);

            MyGL.checkGLES();

            bReal = orbital.real;
            fBrightness = quadratureRadius * quadratureRadius / 2.0f;
            fInverseAzimuthalStepSize = (float) azimuthalTextureSize / 3.14159265359f;
            fInverseQuadratureStepSize = (float) quadratureSteps / quadratureRadius;
            fInverseRadialStepSize = (float) radialTextureSize / maximumRadius;
            fM = (float) orbital.qM;
            // Multiply by 2 because the wave function is squared
            fRadialExponent = (float) (2.0 * radialFunction.getExponentialConstant());
            fRadialPower = (float) (2 * radialFunction.getPowerOfR());
            iAzimuthalSteps = azimuthalTextureSize;
            iOrder = order;
            iQuadratureSteps = quadratureSteps;
            iRadialSteps = radialTextureSize;
        }
    }

    void setupForIntegration(Program program) {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        radialTexture.bindToTexture2D();
        program.setUniform1i("radial", 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        azimuthalTexture.bindToTexture2D();
        program.setUniform1i("azimuthal", 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        quadratureTexture.bindToTexture2D();
        program.setUniform1i("quadrature", 2);

        program.setUniform1i("bReal", bReal ? 1 : 0);
        program.setUniform1f("fBrightness", fBrightness);
        program.setUniform1f("fInverseAzimuthalStepSize", fInverseAzimuthalStepSize);
        program.setUniform1f("fInverseQuadratureStepSize", fInverseQuadratureStepSize);
        program.setUniform1f("fInverseRadialStepSize", fInverseRadialStepSize);
        program.setUniform1f("fM", fM);
        program.setUniform1f("fRadialExponent", fRadialExponent);
        program.setUniform1f("fRadialPower", fRadialPower);
        program.setUniform1i("iAzimuthalSteps", iAzimuthalSteps);
        program.setUniform1i("iOrder", iOrder);
        program.setUniform1i("iQuadratureSteps", iQuadratureSteps);
        program.setUniform1i("iRadialSteps", iRadialSteps);

        MyGL.checkGLES();
    }

    boolean getColor() {
        return orbital.color;
    }

    int getN() {
        return orbital.qN;
    }

    private static float[] functionToBuffer2(Function f, double start, double end, int n) {
        float[] data = new float[2 * n];
        double stepSize = (end - start) / (double) n;
        double x = start;
        float value = (float) f.eval(x);
        for (int i = 0; i < n; ++i) {
            data[2 * i] = value;
            x += stepSize;
            value = (float) f.eval(x);
            data[2 * i + 1] = value;
        }
        return data;
    }
}
