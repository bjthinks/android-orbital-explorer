package com.gputreats.orbitalexplorer;

/**
 * Radial part of the wave function of a one-electron atom.
 * It depends on constants Z, N, and L, and has the form of a polynomial times exp(-Zr/N).
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

class RadialFunction implements Function {

    private final double exponentialConstant;
    private final int powerOfR;
    private final Polynomial oscillatingPart;
    private final double maximumRadius;

    RadialFunction(int inZ, int inN, int inL) {
        double dZ = (double) inZ;
        double dN = (double) inN;

        double radialScaleFactor = 2.0 * dZ / dN;

        exponentialConstant = -radialScaleFactor / 2.0;

        powerOfR = inL;

        double constantFactors = Math.pow(2.0 * dZ / dN, 1.5)
                * Math.sqrt(MyMath.factorial(inN - inL - 1) / (2.0 * dN * MyMath.factorial(inN + inL)));

        oscillatingPart = generalizedLaguerrePolynomial(inN - inL - 1, 2 * inL + 1)
                .rescaleX(radialScaleFactor)
                .multiply(MyMath.fastpow(radialScaleFactor, powerOfR))
                .multiply(constantFactors);

        maximumRadius = MaximumRadiusTable.getMaximumRadius(inN, inL);
    }

    double getExponentialConstant() {
        return exponentialConstant;
    }

    int getPowerOfR() {
        return powerOfR;
    }

    Polynomial getOscillatingPart() {
        return oscillatingPart;
    }

    @Override
    public double eval(double r) {
        return oscillatingPart.eval(r)
                * MyMath.fastpow(r, powerOfR)
                * Math.exp(exponentialConstant * r);
    }

    double getMaximumRadius() {
        return maximumRadius;
    }

    /**
     * generalizedLaguerrePolynomial(n, a) gives the polynomial
     * L_n^a(x), as per the definition on Wikipedia.
     */
    private static Polynomial generalizedLaguerrePolynomial(int n, int a) {
        Polynomial result = new Polynomial();
        for (int i = 0; i <= n; ++i) {
            double coeff = binomial(n + a, n - i) / MyMath.factorial(i);
            if ((i & 1) == 1)
                coeff = -coeff;
            result = result.add(Polynomial.variableToThe(i).multiply(coeff));
        }
        return result;
    }

    static double binomial(int n, int k) {
        return MyMath.factorial(n) / MyMath.factorial(k) / MyMath.factorial(n - k);
    }
}
