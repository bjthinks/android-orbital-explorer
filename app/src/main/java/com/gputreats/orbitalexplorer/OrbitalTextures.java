package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

public class OrbitalTextures {

    private static final int RADIAL_TEXTURE_SIZE = 1024;
    private static final int AZIMUTHAL_TEXTURE_SIZE = 256;

    private AssetManager assets;

    private Orbital orbital;

    private Texture radialTexture;
    private Texture azimuthalTexture;
    private Texture quadratureTexture;

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

    public OrbitalTextures(AssetManager a) {
        assets = a;

        radialTexture     = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        azimuthalTexture  = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);

        MyGL.checkGLES();
    }

    public void loadOrbital(Orbital newOrbital) {
        if (newOrbital.notEquals(orbital)) {
            orbital = newOrbital;
            fM = (float) orbital.M;

            RadialFunction radialFunction = orbital.getRadialFunction();

            // Multiply by 2 because the wave function is squared
            fRadialExponent = 2.0f * (float) radialFunction.getExponentialConstant();
            fRadialPower = 2.0f * radialFunction.getPowerOfR();

            bReal = orbital.real;

            // Load new azimuthal texture
            float[] azimuthalData = MyGL.functionToBuffer2(orbital.getAzimuthalFunction(),
                    0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE);
            azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);
            iAzimuthalSteps = AZIMUTHAL_TEXTURE_SIZE;
            fInverseAzimuthalStepSize = AZIMUTHAL_TEXTURE_SIZE / 3.14159265359f;

            // Load new quadrature texture
            Quadrature quadrature = orbital.getQuadrature();
            iOrder = quadrature.getOrder();
            float[] quadratureData = new QuadratureData(assets, quadrature).get();
            iQuadratureSteps = quadrature.getSteps();
            quadratureTexture.bindToTexture2DAndSetImage(iOrder, iQuadratureSteps, quadratureData);

            // Calculate radius info
            float quadratureRadius = (float) orbital.getRadialFunction().getMaximumRadius();
            fInverseQuadratureStepSize = iQuadratureSteps / quadratureRadius;
            float maxLateral = quadratureData[quadratureData.length - 2];
            float maximumRadius = (float) Math.sqrt(quadratureRadius * quadratureRadius
                    + maxLateral * maxLateral);
            fBrightness = quadratureRadius * quadratureRadius / 2.0f;

            // Load new radial texture
            float[] radialData
                    = MyGL.functionToBuffer2(orbital.getRadialFunction().getOscillatingPart(),
                    0.0, maximumRadius, RADIAL_TEXTURE_SIZE);
            radialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, radialData);
            iRadialSteps = RADIAL_TEXTURE_SIZE;
            fInverseRadialStepSize = iRadialSteps / maximumRadius;

            MyGL.checkGLES();
        }
    }

    public void bindForRendering(Program program) {
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
}
