package com.example;

import com.transvect.orbitalexplorer.Polynomial;
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
                RadialFunction radialFunction = new RadialFunction(N, N, L);
                int quadratureSize = radialFunction.getQuadratureSize();
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();
                int quadraturePoints = radialFunction.getQuadratureOrder();

                float[] quadratureWeights = new float[2 * quadraturePoints * quadratureSize];
                for (int i = 0; i < quadratureSize; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) (quadratureSize - 1);
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant,
                            Polynomial.variableToThe(powerOfR), distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadraturePoints);

                    for (int j = 0; j < quadraturePoints; ++j) {
                        quadratureWeights[2 * quadraturePoints * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * quadraturePoints * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("color-" + N + "-" + L, quadratureWeights);
            }
        }

        // Mono
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {
                RadialFunction radialFunction = new RadialFunction(N, N, L);
                int quadratureSize = radialFunction.getQuadratureSize();
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();
                Polynomial oscillatingPart = radialFunction.getOscillatingPart();
                int quadraturePoints = radialFunction.getQuadratureOrder();

                float[] quadratureWeights = new float[2 * quadraturePoints * quadratureSize];
                for (int i = 0; i < quadratureSize; ++i) {
                    double distanceFromOrigin = radialFunction.getMaximumRadius()
                            * (double) i / (double) (quadratureSize - 1);
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant,
                            new Product(Polynomial.variableToThe(powerOfR), oscillatingPart),
                            distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadraturePoints);

                    for (int j = 0; j < quadraturePoints; ++j) {
                        quadratureWeights[2 * quadraturePoints * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * quadraturePoints * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));
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
