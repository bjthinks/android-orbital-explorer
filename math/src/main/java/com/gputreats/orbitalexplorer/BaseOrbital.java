package com.gputreats.orbitalexplorer;

class BaseOrbital {

    static final int MAX_N = 12;

    final int qZ, qN, qL, qM;
    final boolean real, color;
    private final RadialFunction radialFunction;
    private final AzimuthalFunction azimuthalFunction;
    private final Quadrature quadrature;

    BaseOrbital(int inZ, int inN, int inL, int inM, boolean inReal, boolean inColor) {
        qZ = inZ;
        qN = inN;
        qL = inL;
        qM = inM;
        real = inReal;
        color = inColor;
        radialFunction = new RadialFunction(qZ, qN, qL);
        azimuthalFunction = new AzimuthalFunction(qL, qM);
        quadrature = new Quadrature(qN, qL, color);
    }

    boolean notEquals(BaseOrbital r) {
        return r == null || qZ != r.qZ || qN != r.qN || qL != r.qL || qM != r.qM
                || real != r.real || color != r.color;
    }

    RadialFunction getRadialFunction() {
        return radialFunction;
    }

    AzimuthalFunction getAzimuthalFunction() {
        return azimuthalFunction;
    }

    Quadrature getQuadrature() {
        return quadrature;
    }
}
