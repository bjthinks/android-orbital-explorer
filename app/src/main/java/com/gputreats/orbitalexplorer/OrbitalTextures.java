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
    private float fM;
    private float fMaximumRadius;
    private float fNumAzimuthalSubdivisions = (float) (AZIMUTHAL_TEXTURE_SIZE - 1);
    private float fNumQuadratureSubdivisions;
    private float fNumRadialSubdivisions = (float) (RADIAL_TEXTURE_SIZE - 1);
    private float fQuadratureRadius;
    private float fRadialExponent;
    private float fRadialPower;
    private int iOrder;

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
            float[] azimuthalData = MyMath.functionToBuffer2(orbital.getAzimuthalFunction(),
                    0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE);
            azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

            // Load new quadrature texture
            Quadrature quadrature = orbital.getQuadrature();
            iOrder = quadrature.getOrder();
            float[] quadratureData = QuadratureTable.get(assets, quadrature);
            int quadratureDataSize = quadratureData.length / (4 * iOrder);
            quadratureTexture.bindToTexture2DAndSetImage(iOrder, quadratureDataSize, quadratureData);
            fNumQuadratureSubdivisions = (float) (quadratureDataSize - 1);

            // Calculate radius info
            fQuadratureRadius = (float) orbital.getRadialFunction().getMaximumRadius();
            float maxLateral = quadratureData[quadratureData.length - 2];
            fMaximumRadius = (float) Math.sqrt(fQuadratureRadius * fQuadratureRadius
                    + maxLateral * maxLateral);
            fBrightness = fQuadratureRadius * fQuadratureRadius / 2.0f;

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

        program.setUniform1i("bReal", bReal ? 1 : 0);
        program.setUniform1f("fBrightness", fBrightness);
        program.setUniform1f("fM", fM);
        program.setUniform1f("fMaximumRadius", fMaximumRadius);
        program.setUniform1f("fNumAzimuthalSubdivisions", fNumAzimuthalSubdivisions);
        program.setUniform1f("fNumQuadratureSubdivisions", fNumQuadratureSubdivisions);
        program.setUniform1f("fNumRadialSubdivisions", fNumRadialSubdivisions);
        program.setUniform1f("fQuadratureRadius", fQuadratureRadius);
        program.setUniform1f("fRadialExponent", fRadialExponent);
        program.setUniform1f("fRadialPower", fRadialPower);
        program.setUniform1i("iOrder", iOrder);

        MyGL.checkGLES();
    }
}
