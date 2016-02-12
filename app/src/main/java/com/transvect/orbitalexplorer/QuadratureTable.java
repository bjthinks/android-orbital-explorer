package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class QuadratureTable {

    private static final int QUADRATURE_SIZE = 64;

    private QuadratureTable() {}

    public static float[] get(AssetManager assets, int N, int L) {
        int quadraturePoints = N;
        float[] table = new float[4 * quadraturePoints * QUADRATURE_SIZE];

        String filename = "data-" + N + "-" + L;
        try {
            DataInputStream stream
                    = new DataInputStream(new BufferedInputStream(assets.open(filename)));
            for (int i = 0; i < QUADRATURE_SIZE; ++i) {
                for (int j = 0; j < quadraturePoints; ++j) {
                    table[4 * quadraturePoints * i + 4 * j] = stream.readFloat();
                    table[4 * quadraturePoints * i + 4 * j + 1] = stream.readFloat();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading asset: " + filename);
        }

        // Backfill
        for (int i = 0; i < QUADRATURE_SIZE - 1; ++i) {
            for (int j = 0; j < quadraturePoints; ++j) {
                table[4 * quadraturePoints * i + 4 * j + 2]
                        = table[4 * quadraturePoints * (i + 1) + 4 * j];
                table[4 * quadraturePoints * i + 4 * j + 3]
                        = table[4 * quadraturePoints * (i + 1) + 4 * j + 1];
            }
        }
        for (int j = 0; j < quadraturePoints; ++j) {
            table[4 * quadraturePoints * (QUADRATURE_SIZE - 1) + 4 * j + 2]
                    = table[4 * quadraturePoints * (QUADRATURE_SIZE - 1) + 4 * j];
            table[4 * quadraturePoints * (QUADRATURE_SIZE - 1) + 4 * j + 3]
                    = table[4 * quadraturePoints * (QUADRATURE_SIZE - 1) + 4 * j + 1];
        }

        return table;
    }
}
