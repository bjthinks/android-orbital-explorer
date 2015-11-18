package com.transvect.orbitalexplorer;

/**
 * Azimuthal part of the wave function of a one-electron atom.
 * Theta(theta) depends on constants L and M and is a polynomial in sin & cos of theta
 * This does NOT include the Condon-Shortley phase, which is not needed for this project.
 */

public class AzimuthalFunction {
    private Polynomial mCosThetaPolynomial;
    private int mSinThetaPower;

    public AzimuthalFunction(int L, int M) {
        int absM = Math.abs(M);
        mCosThetaPolynomial = MyMath.legendrePolynomial(L);
        for (int i = 0; i < absM; ++i)
            mCosThetaPolynomial = mCosThetaPolynomial.derivative();
        double constant = Math.sqrt((2.0 * L + 1) / 2.0);
        constant *= Math.sqrt(MyMath.factorial(L - absM) / MyMath.factorial(L + absM));
        mCosThetaPolynomial = mCosThetaPolynomial.multiply(constant);
        mSinThetaPower = absM;
    }

    // Note that theta is "colatitude", i.e. 0 along (0, 0, 1), pi/2 when z=0,
    // and pi along (0, 0, -1).
    public double eval(double theta) {
        return mCosThetaPolynomial.eval(Math.cos(theta))
                * Math.pow(Math.sin(theta), mSinThetaPower);
    }
}
