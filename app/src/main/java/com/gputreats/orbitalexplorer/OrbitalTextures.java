package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

public class OrbitalTextures {

    public static final int RADIAL_TEXTURE_SIZE = 1024;
    public static final int AZIMUTHAL_TEXTURE_SIZE = 256;

    private AssetManager assets;

    public Texture radialTexture;
    public Texture azimuthalTexture;
    public Texture quadratureTexture;
    public int quadratureDataSize;
    public float quadratureRadius, maximumRadius;

    public OrbitalTextures(AssetManager a) {
        assets = a;

        radialTexture     = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        azimuthalTexture  = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);

        MyGL.checkGLES();
    }

    public void loadOrbital(Orbital orbital) {
        // Load new azimuthal texture
        float[] azimuthalData = MyMath.functionToBuffer2(orbital.getAzimuthalFunction(),
                0.0, Math.PI, OrbitalTextures.AZIMUTHAL_TEXTURE_SIZE);
        azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

        // Load new quadrature texture
        Quadrature quadrature = orbital.getQuadrature();
        int order = quadrature.getOrder();
        float[] quadratureData = QuadratureTable.get(assets, quadrature);
        quadratureDataSize = quadratureData.length / (4 * order);
        quadratureTexture.bindToTexture2DAndSetImage(order, quadratureDataSize, quadratureData);

        // Calculate radius info
        quadratureRadius = (float) orbital.getRadialFunction().getMaximumRadius();
        float maxLateral = quadratureData[quadratureData.length - 2];
        maximumRadius = (float) Math.sqrt(quadratureRadius * quadratureRadius
                + maxLateral * maxLateral);

        MyGL.checkGLES();
    }

    public void bindForRendering(Program program) {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        radialTexture.bindToTexture2D();
        program.setUniform("radial", 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        azimuthalTexture.bindToTexture2D();
        program.setUniform("azimuthal", 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        quadratureTexture.bindToTexture2D();
        program.setUniform("quadrature", 2);

        MyGL.checkGLES();
    }
}
