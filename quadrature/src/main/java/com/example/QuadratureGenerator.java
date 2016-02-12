package com.example;

import com.transvect.orbitalexplorer.GaussianQuadrature;
import com.transvect.orbitalexplorer.RadialFunction;
import com.transvect.orbitalexplorer.WeightFunction;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QuadratureGenerator {

    private final int QUADRATURE_SIZE = 64;
    private final double MAXIMUM_RADIUS = 16.0;

    private void writeAsset(String assetname, float data[])
            throws IOException {
        DataOutputStream stream
                = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream("app/src/main/assets/" + assetname)));
        for (int i = 0; i < data.length; ++i)
            stream.writeFloat(data[i]);
        stream.close();
    }

    private void go() throws IOException {
        for (int N = 1; N <= 8; ++N) {
            for (int L = 0; L < N; ++L) {
                int Z = N;
                RadialFunction radialFunction = new RadialFunction(Z, N, L);
                double exponentialConstant = radialFunction.getExponentialConstant();
                int powerOfR = radialFunction.getPowerOfR();
                int quadraturePoints = N;

                float[] quadratureWeights = new float[2 * quadraturePoints * QUADRATURE_SIZE];
                for (int i = 0; i < QUADRATURE_SIZE; ++i) {
                    double distanceFromOrigin = MAXIMUM_RADIUS * (double) i / (double) (QUADRATURE_SIZE - 1);
                    WeightFunction weightFunction
                            = new WeightFunction(exponentialConstant, powerOfR, distanceFromOrigin);
                    GaussianQuadrature GQ = new GaussianQuadrature(weightFunction, quadraturePoints);

                    for (int j = 0; j < quadraturePoints; ++j) {
                        quadratureWeights[2 * quadraturePoints * i + 2 * j]
                                = (float) GQ.getNode(j);
                        quadratureWeights[2 * quadraturePoints * i + 2 * j + 1]
                                = (float) (GQ.getWeight(j) / weightFunction.eval(GQ.getNode(j)));
                    }
                }
                writeAsset("data-" + N + "-" + L, quadratureWeights);
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
