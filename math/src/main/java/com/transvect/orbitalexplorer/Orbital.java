package com.transvect.orbitalexplorer;

public class Orbital {

    public final int Z, N, L, M;
    public final boolean real;
    private RadialFunction radialFunction;
    private Function azimuthalFunction;

    public Orbital(int Z_, int N_, int L_, int M_, boolean real_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;
        real = real_;
        radialFunction = new RadialFunction(Z_, N_, L_);
        azimuthalFunction = new AzimuthalFunction(L_, M_);
    }

    public RadialFunction getRadialFunction() {
        return radialFunction;
    }

    public Function getAzimuthalFunction() {
        return azimuthalFunction;
    }
}
