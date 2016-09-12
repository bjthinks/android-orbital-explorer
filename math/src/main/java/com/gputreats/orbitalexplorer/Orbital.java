package com.gputreats.orbitalexplorer;

class Orbital {

    public final int Z, N, L, M;
    public final boolean real, color;
    private final RadialFunction radialFunction;
    private final AzimuthalFunction azimuthalFunction;
    private final Quadrature quadrature;

    Orbital(int Z_, int N_, int L_, int M_, boolean real_, boolean color_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;
        real = real_;
        color = color_;
        radialFunction = new RadialFunction(Z, N, L);
        azimuthalFunction = new AzimuthalFunction(L, M);
        quadrature = new Quadrature(N, L, color);
    }

    public boolean notEquals(Orbital r) {
        return (r == null || Z != r.Z || N != r.N || L != r.L || M != r.M
                || real != r.real || color != r.color);
    }

    public RadialFunction getRadialFunction() {
        return radialFunction;
    }

    public AzimuthalFunction getAzimuthalFunction() {
        return azimuthalFunction;
    }

    public Quadrature getQuadrature() {
        return quadrature;
    }
}
