package com.transvect.orbitalexplorer;

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

public class RadialFunction implements Function {

    private double exponentialConstant;
    private int powerOfR;
    private Polynomial oscillatingPart;
    private int quadratureOrder;
    private double maximumRadius;
    private double outer90PercentRadialL2Integral;

    public RadialFunction(int Z, int N, int L) {
        double dZ = (double) Z;
        double dN = (double) N;

        double radialScaleFactor = 2.0 * dZ / dN;

        exponentialConstant = -radialScaleFactor / 2.0;

        powerOfR = L;

        double constantFactors = Math.pow(2.0 * dZ / dN, 1.5)
                * Math.sqrt(MyMath.factorial(N - L - 1) / (2.0 * dN * MyMath.factorial(N + L)));

        oscillatingPart = GeneralizedLaguerrePolynomial.generate(N - L - 1, 2 * L + 1)
                .rescaleX(radialScaleFactor)
                .multiply(Math.pow(radialScaleFactor, powerOfR))
                .multiply(constantFactors);

        // An important design choice is to make the quadrature order independent of M. This
        // lets us re-use quadrature nodes and weights for all orbitals with the same
        // (Z, N, L). Combined with the design choice to set Z = N always, this means we
        // only need nodes and weights for each of the (N, L) pairs.
        // Experiments show that a very simple formula here suffices:
        // N     = pretty good, slight defects near center at some viewing angles
        // N + 1 = extremely good, defects present but not
        //         visible without direct comparison to N + 2
        // N + 2 = essentially perfect, visually identical to all higher orders for all but
        //         a few very specific corner cases
        quadratureOrder = N + 1;

        double r;
        int consecutiveSmall = 0;
        for (r = 5.0; consecutiveSmall < 5; r += 0.1) {
            double f = eval(r);
            if (Math.abs(r * f * f) < 1e-4)
                ++consecutiveSmall;
            else
                consecutiveSmall = 0;
        }
        maximumRadius = r;

        outer90PercentRadialL2Integral = 0.0;
        for (r = 0.1 * maximumRadius; r < maximumRadius; r += 0.1) {
            double f = eval(r);
            outer90PercentRadialL2Integral += f * f;
        }
    }

    public double getExponentialConstant() {
        return exponentialConstant;
    }

    public int getPowerOfR() {
        return powerOfR;
    }

    public Polynomial getOscillatingPart() {
        return oscillatingPart;
    }

    public double eval(double r) {
        return oscillatingPart.eval(r)
                * Math.pow(r, powerOfR)
                * Math.exp(exponentialConstant * r);
    }

    public double getMaximumRadius() {
        return maximumRadius;
    }

    public double getOuter90PercentRadialL2Integral() {
        return outer90PercentRadialL2Integral;
    }

    public int getQuadratureOrder() {
        return quadratureOrder;
    }
}
