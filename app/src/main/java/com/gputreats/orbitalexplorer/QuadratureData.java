package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

class QuadratureData {

    private final float[] data;

    QuadratureData(AssetManager assets, Quadrature quadrature) {
        int order = quadrature.getOrder();
        int steps = quadrature.getSteps();
        data = new float[4 * order * steps];

        String filename = "data/";
        filename += quadrature.color ? "color" : "mono";
        filename += "-" + quadrature.qN + '-' + quadrature.qL;

        DataInputStream stream;
        try {
            stream = new DataInputStream(new BufferedInputStream(assets.open(filename)));
        } catch (IOException ignored) {
            throw new RuntimeException("Error opening asset: " + filename);
        }
        try {
            for (int i = 0; i <= steps; ++i) {
                for (int j = 0; j < order; ++j) {
                    float node = stream.readFloat();
                    float weight = stream.readFloat();
                    if (i != steps) {
                        data[4 * order * i + 4 * j] = node;
                        data[4 * order * i + 4 * j + 1] = weight;
                    }
                    if (i != 0) {
                        data[4 * order * (i - 1) + 4 * j + 2] = node;
                        data[4 * order * (i - 1) + 4 * j + 3] = weight;
                    }
                }
            }
        } catch (IOException ignored) {
            throw new RuntimeException("Error reading from asset: " + filename);
        }
        try {
            stream.close();
        } catch (IOException ignored) {
            throw new RuntimeException("Error closing asset: " + filename);
        }
    }

    float[] get() {
        return data;
    }
}
