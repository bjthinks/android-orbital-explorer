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

    private float fQuadratureRadius;
    private float fMaximumRadius;
    private static final float fNumRadialSubdivisions = (float) (RADIAL_TEXTURE_SIZE - 1);
    private float fExponentialConstant;
    private static final float fNumAzimuthalSubdivisions = (float) (AZIMUTHAL_TEXTURE_SIZE - 1);
    private float fNumQuadratureSubdivisions;
    private int iOrder;
    private int quadratureDataSize;
    private int realOrbital;
    private float radialPower;
    private float brightness;

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

            RadialFunction radialFunction = orbital.getRadialFunction();

            // Multiply by 2 because the wave function is squared
            fExponentialConstant = 2.0f * (float) radialFunction.getExponentialConstant();
            radialPower = 2.0f * radialFunction.getPowerOfR();

            realOrbital = orbital.real ? 1 : 0;

            // Load new azimuthal texture
            float[] azimuthalData = MyMath.functionToBuffer2(orbital.getAzimuthalFunction(),
                    0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE);
            azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

            // Load new quadrature texture
            Quadrature quadrature = orbital.getQuadrature();
            iOrder = quadrature.getOrder();
            float[] quadratureData = QuadratureTable.get(assets, quadrature);
            quadratureDataSize = quadratureData.length / (4 * iOrder);
            quadratureTexture.bindToTexture2DAndSetImage(iOrder, quadratureDataSize, quadratureData);
            fNumQuadratureSubdivisions = (float) (quadratureDataSize - 1);

            // Calculate radius info
            fQuadratureRadius = (float) orbital.getRadialFunction().getMaximumRadius();
            float maxLateral = quadratureData[quadratureData.length - 2];
            fMaximumRadius = (float) Math.sqrt(fQuadratureRadius * fQuadratureRadius
                    + maxLateral * maxLateral);
            brightness = fQuadratureRadius * fQuadratureRadius / 2.0f;

            // Load new radial texture
            float[] radialData
                    = MyMath.functionToBuffer2(orbital.getRadialFunction().getOscillatingPart(),
                    0.0, fMaximumRadius, RADIAL_TEXTURE_SIZE);
            radialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, radialData);

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

        program.setUniform1f("fQuadratureRadius", fQuadratureRadius);
        program.setUniform1f("fMaximumRadius", fMaximumRadius);
        program.setUniform1f("fNumRadialSubdivisions", fNumRadialSubdivisions);
        program.setUniform1f("fExponentialConstant", fExponentialConstant);
        program.setUniform1f("fNumAzimuthalSubdivisions", fNumAzimuthalSubdivisions);
        program.setUniform1f("fNumQuadratureSubdivisions", fNumQuadratureSubdivisions);
        program.setUniform1i("iOrder", iOrder);

        program.setUniform1i("realOrbital", realOrbital);
        program.setUniform1f("powerOfR", radialPower);
        program.setUniform1f("brightness", brightness);
        program.setUniform1f("M", (float) orbital.M);

        MyGL.checkGLES();
    }
}
