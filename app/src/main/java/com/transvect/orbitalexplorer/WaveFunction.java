package com.transvect.orbitalexplorer;

public class WaveFunction {

    private Function mRadialFunction;
    private Function mAzimuthalFunction;
    private int mM;

    public WaveFunction(int Z, int N, int L, int M) {
        mRadialFunction = new RadialFunction(Z, N, L);
        mAzimuthalFunction = new AzimuthalFunction(L, M);
        mM = M;
    }

    public Function getRadialFunction() {
        return mRadialFunction;
    }

    public Function getAzimuthalFunction() {
        return mAzimuthalFunction;
    }

    public int getM() {
        return mM;
    }
}
