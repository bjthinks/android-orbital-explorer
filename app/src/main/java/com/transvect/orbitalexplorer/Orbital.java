package com.transvect.orbitalexplorer;

public class Orbital {

    private final double MAXIMUM_RADIUS = 16.0;
    private final int RADIAL_TEXTURE_SIZE = 256;
    private final int AZIMUTHAL_TEXTURE_SIZE = 256;
    private final int QUADRATURE_SIZE = 64;

    final public int Z, N, L, M;
    private WaveFunction waveFunction;

    private double exponentialConstant;
    private double powerOfR;
    private int quadraturePoints;

    public Orbital(int Z_, int N_, int L_, int M_) {
        Z = Z_;
        N = N_;
        L = L_;
        M = M_;

        waveFunction = new WaveFunction(Z, N, L, M);

        exponentialConstant = waveFunction.getRadialFunction().exponentialConstant();
        powerOfR = waveFunction.getRadialFunction().powerOfR();

        int hardness1 = N - L;      // radial nodes make rendering hard
        int hardness2 = L - M + 1;  // azimuthal nodes make rendering hard
        int greaterHardness = Math.max(hardness1, hardness2);
        int lesserHardness  = Math.min(hardness1, hardness2);
        // The below formula comes from tons of experimentation and seems to give
        // eye-accurate renderings of all orbitals.
        quadraturePoints = greaterHardness + lesserHardness / 2 + M / 3;
    }

    float[] getRadialData() {
        return RenderStage.functionToBuffer2(waveFunction.getRadialFunction().oscillatingPart(),
                0.0, MAXIMUM_RADIUS, RADIAL_TEXTURE_SIZE - 1);
    }

    float[] getAzimuthalData() {
        return RenderStage.functionToBuffer2(waveFunction.getAzimuthalFunction(),
                0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);
    }

    int getQuadraturePoints() {
        return quadraturePoints;
    }

    float[] getQuadratureData() {
        // Set up Gaussian Quadrature
        float[] quadratureWeights = new float[4 * quadraturePoints * QUADRATURE_SIZE];
        for (int i = 0; i < QUADRATURE_SIZE; ++i) {
            double distanceFromOrigin = MAXIMUM_RADIUS * (double) i / (double) (QUADRATURE_SIZE - 1);
            WeightFunction weightFunction
                    = new WeightFunction(distanceFromOrigin);
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

    private class WeightFunction implements Function {
        private double mDistanceFromOrigin;

        public WeightFunction(double distanceFromOrigin) {
            mDistanceFromOrigin = distanceFromOrigin;
        }

        public double eval(double x) {
            if (x < 0.0)
                return 0.0;

            double r = Math.sqrt(mDistanceFromOrigin * mDistanceFromOrigin + x * x);
            // Multiply by 2 because the wave function is squared
            double value = Math.exp(2.0 * exponentialConstant * r);
            // Multiply by 2 because the wave function is squared
            value *= Math.pow(r, 2.0 * powerOfR);

            if (x == 0.0)
                value *= 0.5;

            return value;
        }
    }
}
