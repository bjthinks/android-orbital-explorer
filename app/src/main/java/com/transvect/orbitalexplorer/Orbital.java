package com.transvect.orbitalexplorer;

public class Orbital {

    private final double MAXIMUM_RADIUS = 16.0;
    private final int RADIAL_TEXTURE_SIZE = 256;
    private final int AZIMUTHAL_TEXTURE_SIZE = 256;
    private final int QUADRATURE_SIZE = 64;

    public final int Z, N, L, M;
    private WaveFunction waveFunction;

    private double exponentialConstant;
    private int powerOfR;
    private int quadraturePoints;

    public Orbital(int Z_, int N_, int L_, int M_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;

        waveFunction = new WaveFunction(Z, N, L, M);

        exponentialConstant = waveFunction.getRadialFunction().getExponentialConstant();
        powerOfR = waveFunction.getRadialFunction().getPowerOfR();

        int difficulty1 = N - L;      // radial nodes make rendering hard
        int difficulty2 = L - M + 1;  // azimuthal nodes make rendering hard
        int greaterDifficulty = Math.max(difficulty1, difficulty2);
        int lesserDifficulty  = Math.min(difficulty1, difficulty2);

        // The below formula comes from tons of experimentation and seems to give
        // eye-accurate renderings of all orbitals.
        quadraturePoints = greaterDifficulty + lesserDifficulty / 2 + M / 3;
    }

    public double getMaximumRadius() {
        return MAXIMUM_RADIUS;
    }

    public double getRadialExponent() {
        return waveFunction.getRadialFunction().getExponentialConstant();
    }

    public int getRadialPower() {
        return waveFunction.getRadialFunction().getPowerOfR();
    }

    public float[] getRadialData() {
        return RenderStage.functionToBuffer2(waveFunction.getRadialFunction().getOscillatingPart(),
                0.0, MAXIMUM_RADIUS, RADIAL_TEXTURE_SIZE - 1);
    }

    public float[] getAzimuthalData() {
        return RenderStage.functionToBuffer2(waveFunction.getAzimuthalFunction(),
                0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);
    }

    public int getM() {
        return waveFunction.getM();
    }

    public int getNumQuadraturePoints() {
        return quadraturePoints;
    }

    public float[] getQuadratureData() {
        // Set up Gaussian Quadrature
        float[] quadratureWeights = new float[4 * quadraturePoints * QUADRATURE_SIZE];
        for (int i = 0; i < QUADRATURE_SIZE; ++i) {
            double distanceFromOrigin = MAXIMUM_RADIUS * (double) i / (double) (QUADRATURE_SIZE - 1);
            WeightFunction weightFunction
                    = new WeightFunction(exponentialConstant, powerOfR, distanceFromOrigin);
            GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadraturePoints);

            for (int j = 0; j < quadraturePoints; ++j) {
                quadratureWeights[4 * quadraturePoints * i + 4 * j]
                        = (float) GQ.getNode(j);
                quadratureWeights[4 * quadraturePoints * i + 4 * j + 1]
                        = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));

                // Backfill previous
                if (i > 0) {
                    quadratureWeights[4 * quadraturePoints * (i - 1) + 4 * j + 2]
                            = quadratureWeights[4 * quadraturePoints * i + 4 * j];
                    quadratureWeights[4 * quadraturePoints * (i - 1) + 4 * j + 3]
                            = quadratureWeights[4 * quadraturePoints * i + 4 * j + 1];
                }
            }
        }
        return quadratureWeights;
    }
}
