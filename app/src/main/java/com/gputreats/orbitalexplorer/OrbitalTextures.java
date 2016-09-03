package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

class OrbitalTextures {

    private AssetManager assets;

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

    OrbitalTextures(Context context) {
        assets = context.getAssets();
    }

    void onSurfaceCreated() {
        radialTexture     = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        azimuthalTexture  = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);

        MyGL.checkGLES();
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
            float maximumRadius = (float) Math.sqrt(quadratureRadius * quadratureRadius
                    + maxLateral * maxLateral);

            // Load new radial texture
            final int RADIAL_TEXTURE_SIZE = 1024;
            float[] radialData = MyGL.functionToBuffer2(radialFunction.getOscillatingPart(),
                    0.0, maximumRadius, RADIAL_TEXTURE_SIZE);
            radialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, radialData);

            // Load new azimuthal texture
            final int AZIMUTHAL_TEXTURE_SIZE = 256;
            float[] azimuthalData = MyGL.functionToBuffer2(azimuthalFunction,
                    0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE);
            azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

            MyGL.checkGLES();

            bReal = orbital.real;
            fBrightness = quadratureRadius * quadratureRadius / 2f;
            fInverseAzimuthalStepSize = AZIMUTHAL_TEXTURE_SIZE / 3.14159265359f;
            fInverseQuadratureStepSize = quadratureSteps / quadratureRadius;
            fInverseRadialStepSize = RADIAL_TEXTURE_SIZE / maximumRadius;
            fM = (float) orbital.M;
            // Multiply by 2 because the wave function is squared
            fRadialExponent = 2 * (float) radialFunction.getExponentialConstant();
            fRadialPower = 2 * radialFunction.getPowerOfR();
            iAzimuthalSteps = AZIMUTHAL_TEXTURE_SIZE;
            iOrder = order;
            iQuadratureSteps = quadratureSteps;
            iRadialSteps = RADIAL_TEXTURE_SIZE;
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
        return orbital.N;
    }

    float getRadius() {
        return (float) orbital.getRadialFunction().getMaximumRadius();
    }
}
