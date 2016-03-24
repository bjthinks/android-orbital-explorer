package com.example;

import com.transvect.orbitalexplorer.Polynomial;
import com.transvect.orbitalexplorer.Quadrature;
import com.transvect.orbitalexplorer.RadialFunction;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class QuadratureGenerator {

    private void writeAsset(String assetname, float data[])
            throws IOException {
        DataOutputStream stream
                = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream("app/src/main/assets/data/" + assetname)));
        for (float d : data)
            stream.writeFloat(d);
        stream.close();
    }

    private void go() throws IOException {
        // Color
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {

                RadialFunction radialFunction = new RadialFunction(1, N, L);
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();

                Quadrature quadrature = new Quadrature(N);
                int quadratureOrder = quadrature.getOrder();
                int quadratureSize = quadrature.getSize();

                float[] quadratureWeights = new float[2 * quadratureOrder * quadratureSize];
                for (int i = 0; i < quadratureSize; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) (quadratureSize - 1);
                    WeightFunction weightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR), distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadratureOrder);

                    for (int j = 0; j < quadratureOrder; ++j) {
                        quadratureWeights[2 * quadratureOrder * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * quadratureOrder * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("color-" + N + "-" + L, quadratureWeights);
            }
        }

        // Mono
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {

                RadialFunction radialFunction = new RadialFunction(1, N, L);
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();
                Polynomial oscillatingPart = radialFunction.getOscillatingPart();

                Quadrature quadrature = new Quadrature(N);
                int quadratureOrder = quadrature.getOrder();
                int quadratureSize = quadrature.getSize();

                float[] quadratureWeights = new float[2 * quadratureOrder * quadratureSize];
                for (int i = 0; i < quadratureSize; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) (quadratureSize - 1);
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant,
                            new Product(Polynomial.variableToThe(powerOfR), oscillatingPart),
                            distanceFromOrigin);
                    WeightFunction simpleWeightFunction = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR), distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadratureOrder);

                    for (int j = 0; j < quadratureOrder; ++j) {
                        quadratureWeights[2 * quadratureOrder * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * quadratureOrder * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j)
                                / simpleWeightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("mono-" + N + "-" + L, quadratureWeights);
            }
        }
    }

    public static void main(String args[]) {
        QuadratureGenerator q = new QuadratureGenerator();
        try {
            q.go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
