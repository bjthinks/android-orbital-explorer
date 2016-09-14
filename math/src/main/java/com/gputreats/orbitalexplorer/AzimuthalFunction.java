package com.gputreats.orbitalexplorer;

/**
 * Azimuthal part of the wave function of a one-electron atom.
 * It depends on constants L and M and is a polynomial in sin & cos of theta.
 * This does NOT include the Condon-Shortley phase, which is not needed for the orbital
 * explorer.
 * This function can always be written as a power of sin(theta) times a polynomial in
 * cos(theta), which we do here.
 */

class AzimuthalFunction implements Function {

    private final int sinThetaPower;
    private Polynomial cosThetaPolynomial;

    AzimuthalFunction(int qL, int qM) {
        int absM = Math.abs(qM);
        cosThetaPolynomial = legendrePolynomial(qL);
        for (int i = 0; i < absM; ++i)
            cosThetaPolynomial = cosThetaPolynomial.derivative();
        double constant = Math.sqrt((double) (2 * qL + 1) / 2.0);
        constant *= Math.sqrt(MyMath.factorial(qL - absM) / MyMath.factorial(qL + absM));
        cosThetaPolynomial = cosThetaPolynomial.multiply(constant);
        sinThetaPower = absM;
    }

    // Note that theta is "colatitude", i.e. 0 along (0, 0, 1), pi/2 when z=0,
    // and pi along (0, 0, -1).
    @Override
    public double eval(double theta) {
        return cosThetaPolynomial.eval(Math.cos(theta))
                * MyMath.fastpow(Math.sin(theta), sinThetaPower);
    }

    /**
     * legendrePolynomial gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n], as per the definition on Wikipedia.
     */
    private static Polynomial legendrePolynomial(int qL) {
        Polynomial result = Polynomial.variableToThe(2).subtract(1.0).pow(qL);

        for (int i = 0; i < qL; ++i)
            result = result.derivative().multiply(1.0 / (double) (2 * (i + 1)));

        return result;
    }
}
