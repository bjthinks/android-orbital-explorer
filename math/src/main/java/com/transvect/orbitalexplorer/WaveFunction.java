package com.transvect.orbitalexplorer;

public class WaveFunction {

    private RadialFunction radialFunction;
    private Function azimuthalFunction;
    private int M;

    public WaveFunction(int Z_, int N_, int L_, int M_) {
        radialFunction = new RadialFunction(Z_, N_, L_);
        azimuthalFunction = new AzimuthalFunction(L_, M_);
        M = M_;
    }

    public RadialFunction getRadialFunction() {
        return radialFunction;
    }

    public Function getAzimuthalFunction() {
        return azimuthalFunction;
    }

    public int getM() {
        return M;
    }
}
