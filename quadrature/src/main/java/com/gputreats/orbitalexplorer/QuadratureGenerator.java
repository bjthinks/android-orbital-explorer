package com.gputreats.orbitalexplorer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

enum QuadratureGenerator {
    ;

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
        for (int qN = 1; qN <= BaseOrbital.MAX_N; ++qN) {
            for (int qL = 0; qL < qN; ++qL) {

                RadialFunction radialFunction = new RadialFunction(1, qN, qL);
                double radialScaleFactor = radialFunction.getRadialScaleFactor();
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();

                Quadrature quadrature = new Quadrature(qN, qL, true);
                int order = quadrature.getOrder();
                int steps = quadrature.getSteps();

                float[] quadratureWeights = new float[2 * order * (steps + 1)];
                for (int i = 0; i <= steps; ++i) {
                    System.out.println("color " + qN + " " + qL + " " + i);
                    double maxRadius = radialFunction.getMaximumRadius();
                    double distanceFromOrigin = maxRadius * (double) i / (double) steps;
                    double minLength = Math.sqrt(maxRadius * maxRadius -
                            distanceFromOrigin * distanceFromOrigin);
                    if (minLength < 2.0)
                        minLength = 2.0;
                    WeightFunction weightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR).rescaleX(radialScaleFactor),
                            distanceFromOrigin);
                    GaussianQuadrature gq = new GaussianQuadrature(weightFunction, order, minLength);

                    for (int j = 0; j < order; ++j) {
                        quadratureWeights[2 * order * i + 2 * j]
                                = (float) gq.getNode(j);
                        quadratureWeights[2 * order * i + 2 * j + 1]
                                = (float) (gq.getWeight(j) / weightFunction.eval(gq.getNode(j)));
                    }
                    if (i == 0 || i == steps) {
                        System.out.println("Color N=" + qN + " L=" + qL + " step=" + i);
                        for (int j = 0; j < order; ++j)
                            System.out.print(gq.getNode(j) + " ");
                        System.out.println();
                        for (int j = 0; j < order; ++j)
                            System.out.print(gq.getWeight(j) + " ");
                        System.out.println();
                    }
                }
                writeAsset("color-" + qN + '-' + qL, quadratureWeights);

                // Mono
                Polynomial oscillatingPart = radialFunction.getOscillatingPart();

                quadrature = new Quadrature(qN, qL, false);
                order = quadrature.getOrder();
                steps = quadrature.getSteps();

                quadratureWeights = new float[2 * order * (steps + 1)];
                for (int i = 0; i <= steps; ++i) {
                    System.out.println("mono " + qN + " " + qL + " " + i);
                    double maxRadius = radialFunction.getMaximumRadius();
                    double distanceFromOrigin = maxRadius * (double) i / (double) steps;
                    double minLength = Math.sqrt(maxRadius * maxRadius -
                            distanceFromOrigin * distanceFromOrigin);
                    if (minLength < 2.0)
                        minLength = 2.0;
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant,
                            new Product(Polynomial.variableToThe(powerOfR)
                                    .rescaleX(radialScaleFactor),
                                    oscillatingPart), distanceFromOrigin);
                    Function simpleWeightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR).rescaleX(radialScaleFactor),
                            distanceFromOrigin);
                    GaussianQuadrature gq = new GaussianQuadrature(weightFunction, order, minLength);

                    for (int j = 0; j < order; ++j) {
                        quadratureWeights[2 * order * i + 2 * j]
                                = (float) gq.getNode(j);
                        quadratureWeights[2 * order * i + 2 * j + 1]
                                = (float) (gq.getWeight(j)
                                / simpleWeightFunction.eval(gq.getNode(j)));
                    }
                    if (i == 0 || i == steps) {
                        System.out.println("Mono N=" + qN + " L=" + qL + " step=" + i);
                        for (int j = 0; j < order; ++j)
                            System.out.print(gq.getNode(j) + " ");
                        System.out.println();
                        for (int j = 0; j < order; ++j)
                            System.out.print(gq.getWeight(j) + " ");
                        System.out.println();
                    }
                }
                writeAsset("mono-" + qN + '-' + qL, quadratureWeights);
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
