package com.transvect.orbitalexplorer;

/**
 * Azimuthal part of the wave function of a one-electron atom.
 * It depends on constants L and M and is a polynomial in sin & cos of theta.
 * This does NOT include the Condon-Shortley phase, which is not needed for the orbital
 * explorer.
 * This function can always be written as a power of sin(theta) times a polynomial in
 * cos(theta), which we do here.
 */

public class AzimuthalFunction implements Function {

    private int sinThetaPower;
    private Polynomial cosThetaPolynomial;

    public AzimuthalFunction(int L, int M) {
        int absM = Math.abs(M);
        cosThetaPolynomial = LegendrePolynomial.generate(L);
        for (int i = 0; i < absM; ++i)
            cosThetaPolynomial = cosThetaPolynomial.derivative();
        double constant = Math.sqrt((2.0 * L + 1) / 2.0);
        constant *= Math.sqrt(MyMath.factorial(L - absM) / MyMath.factorial(L + absM));
        cosThetaPolynomial = cosThetaPolynomial.multiply(constant);
        sinThetaPower = absM;
    }

    // Note that theta is "colatitude", i.e. 0 along (0, 0, 1), pi/2 when z=0,
    // and pi along (0, 0, -1).
    public double eval(double theta) {
        return cosThetaPolynomial.eval(Math.cos(theta))
                * Math.pow(Math.sin(theta), sinThetaPower);
    }
}
