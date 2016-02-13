package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;

public class Orbital {

    private final double MAXIMUM_RADIUS = 16.0;
    private final int RADIAL_TEXTURE_SIZE = 256;
    private final int AZIMUTHAL_TEXTURE_SIZE = 256;

    public final int Z, N, L, M;
    public final WaveFunction waveFunction;

    public Orbital(int Z_, int N_, int L_, int M_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;

        waveFunction = new WaveFunction(Z, N, L, M);
    }

    public double getMaximumRadius() {
        return MAXIMUM_RADIUS;
    }

    public float[] getRadialData() {
        return RenderStage.functionToBuffer2(waveFunction.getRadialFunction().getOscillatingPart(),
                0.0, MAXIMUM_RADIUS, RADIAL_TEXTURE_SIZE - 1);
    }

    public float[] getAzimuthalData() {
        return RenderStage.functionToBuffer2(waveFunction.getAzimuthalFunction(),
                0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);
    }

    public int getNumQuadraturePoints() {
        // This is pretty good, and limits visual artifacts to being rather subtle
        return N;
    }
}
