package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class QuadratureData {

    private float[] data;

    public QuadratureData(AssetManager assets, Quadrature quadrature) {
        int order = quadrature.getOrder();
        int steps = quadrature.getSteps();
        data = new float[4 * order * steps];

        String filename = "data/";
        if (quadrature.color)
            filename += "color";
        else
            filename += "mono";
        filename += "-" + quadrature.N + "-" + quadrature.L;

        DataInputStream stream;
        try {
            stream = new DataInputStream(new BufferedInputStream(assets.open(filename)));
        } catch (IOException e) {
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
        } catch (IOException e) {
            throw new RuntimeException("Error reading from asset: " + filename);
        }
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing asset: " + filename);
        }
    }

    public float[] get() {
        return data;
    }
}
