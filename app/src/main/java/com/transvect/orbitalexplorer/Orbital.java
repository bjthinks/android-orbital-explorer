package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;

public class Orbital {

    private final double MAXIMUM_RADIUS = 16.0;

    public final WaveFunction waveFunction;

    public Orbital(int Z, int N, int L, int M) {
        waveFunction = new WaveFunction(Z, N, L, M);
    }

    public double getMaximumRadius() {
        return MAXIMUM_RADIUS;
    }

    public int getNumQuadraturePoints() {
        // This is pretty good, and limits visual artifacts to being rather subtle
        return waveFunction.N;
    }
}
