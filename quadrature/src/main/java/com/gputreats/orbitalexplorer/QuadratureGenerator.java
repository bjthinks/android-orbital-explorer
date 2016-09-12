package com.gputreats.orbitalexplorer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class QuadratureGenerator {

    private QuadratureGenerator() {}

    private static void writeAsset(String assetname, float[] data)
            throws IOException {
        DataOutputStream stream
                = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream("app/src/main/assets/data/" + assetname)));
        for (float d : data)
            stream.writeFloat(d);
        stream.close();
    }

    private static void go() throws IOException {
        // Color
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {

                RadialFunction radialFunction = new RadialFunction(1, N, L);
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();

                Quadrature quadrature = new Quadrature(N, L, true);
                int order = quadrature.getOrder();
                int steps = quadrature.getSteps();

                float[] quadratureWeights = new float[2 * order * (steps + 1)];
                for (int i = 0; i <= steps; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) steps;
                    WeightFunction weightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR), distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, order);

                    for (int j = 0; j < order; ++j) {
                        quadratureWeights[2 * order * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * order * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("color-" + N + '-' + L, quadratureWeights);
            }
        }

        // Mono
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {

                RadialFunction radialFunction = new RadialFunction(1, N, L);
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();
                Polynomial oscillatingPart = radialFunction.getOscillatingPart();

                Quadrature quadrature = new Quadrature(N, L, false);
                int order = quadrature.getOrder();
                int steps = quadrature.getSteps();

                float[] quadratureWeights = new float[2 * order * (steps + 1)];
                for (int i = 0; i <= steps; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) steps;
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant,
                            new Product(Polynomial.variableToThe(powerOfR), oscillatingPart),
                            distanceFromOrigin);
                    Function simpleWeightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR), distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, order);

                    for (int j = 0; j < order; ++j) {
                        quadratureWeights[2 * order * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * order * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j)
                                / simpleWeightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("mono-" + N + '-' + L, quadratureWeights);
            }
        }
    }

    public static void main(String[] args) {
        try {
            go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
