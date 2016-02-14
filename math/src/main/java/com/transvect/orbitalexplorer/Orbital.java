package com.transvect.orbitalexplorer;

public class Orbital {

    public final int Z, N, L, M;
    private RadialFunction radialFunction;
    private Function azimuthalFunction;

    public Orbital(int Z_, int N_, int L_, int M_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;
        radialFunction = new RadialFunction(Z_, N_, L_);
        azimuthalFunction = new AzimuthalFunction(L_, M_);
    }

    public RadialFunction getRadialFunction() {
        return radialFunction;
    }

    public Function getAzimuthalFunction() {
        return azimuthalFunction;
    }

    public int getQuadratureOrder() {
        // This is pretty good, and limits visual artifacts to being rather subtle
        return N;
    }
}
