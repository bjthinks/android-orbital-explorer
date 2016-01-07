package com.transvect.orbitalexplorer;

/**
 * Radial part of the wave function of a one-electron atom.
 * R(r) depends on constants Z, N, and L, and has the form of a polynomial times exp(-Zr/N).
 * We take the radial coordinate r to be in units of a_mu, which is approximately the Bohr
 * radius a_0, but with a small adjustment to take into account the reduced mass of the
 * nucleus-electron system: a_mu = a_0 * (1 + m_electron / m_nucleus).
 * For simplicity, this program ignores the fact that a_mu varies (very) slightly with the
 * nuclear mass, and uses it as a fixed length scale.
 *
 * R_{Z,N,L}(r) = (2Z/N)^1.5 sqrt((N-L-1)! / (2N (N+L)!)) * exp(-Zr/N) * (2Zr/N)^L *
 *                L_{N-L-1}^{2L+1}(2Zr/N)
 * Where L is a generalized (or "associated") Laguerre polynomial.
 */

public class RadialFunction implements Function {
    private double mExponentialConstant;
    private Function mNonExponentialPart;

    public RadialFunction(int Z, int N, int L) {
        double dZ = (double) Z;
        double dN = (double) N;

        double radialScaleFactor = 2.0 * dZ / dN;

        mExponentialConstant = -radialScaleFactor / 2.0;

        double constantFactors = Math.pow(2.0 * dZ / dN, 1.5)
                * Math.sqrt(MyMath.factorial(N - L - 1) / (2.0 * dN * MyMath.factorial(N + L)));

        Polynomial polynomialPart = Polynomial.variableToThe(L).multiply(constantFactors)
                .multiply(MyMath.generalizedLaguerrePolynomial(N - L - 1, 2 * L + 1));

        mNonExponentialPart = polynomialPart.rescaleX(radialScaleFactor);
    }

    public double exponentialConstant() {
        return mExponentialConstant;
    }

    public Function nonExponentialPart() {
        return mNonExponentialPart;
    }

    public double eval(double r) {
        return mNonExponentialPart.eval(r) * Math.exp(exponentialConstant() * r);
    }
}
