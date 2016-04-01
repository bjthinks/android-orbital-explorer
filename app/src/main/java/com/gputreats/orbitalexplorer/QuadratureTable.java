package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class QuadratureTable {

    private QuadratureTable() {}

    public static float[] get(AssetManager assets, Quadrature quadrature) {
        int quadraturePoints = quadrature.getOrder();
        int quadratureSize = quadrature.getSize();
        float[] table = new float[4 * quadraturePoints * quadratureSize];

        String filename = "data/";
        if (quadrature.color)
            filename += "color";
        else
            filename += "mono";
        filename += "-" + quadrature.N + "-" + quadrature.L;

        try {
            DataInputStream stream
                    = new DataInputStream(new BufferedInputStream(assets.open(filename)));
            for (int i = 0; i < quadratureSize; ++i) {
                for (int j = 0; j < quadraturePoints; ++j) {
                    table[4 * quadraturePoints * i + 4 * j] = stream.readFloat();
                    table[4 * quadraturePoints * i + 4 * j + 1] = stream.readFloat();
                }
            }
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error reading asset: " + filename);
        }

        // Backfill
        for (int i = 0; i < quadratureSize - 1; ++i) {
            for (int j = 0; j < quadraturePoints; ++j) {
                table[4 * quadraturePoints * i + 4 * j + 2]
                        = table[4 * quadraturePoints * (i + 1) + 4 * j];
                table[4 * quadraturePoints * i + 4 * j + 3]
                        = table[4 * quadraturePoints * (i + 1) + 4 * j + 1];
            }
        }
        for (int j = 0; j < quadraturePoints; ++j) {
            table[4 * quadraturePoints * (quadratureSize - 1) + 4 * j + 2]
                    = table[4 * quadraturePoints * (quadratureSize - 1) + 4 * j];
            table[4 * quadraturePoints * (quadratureSize - 1) + 4 * j + 3]
                    = table[4 * quadraturePoints * (quadratureSize - 1) + 4 * j + 1];
        }

        return table;
    }
}
