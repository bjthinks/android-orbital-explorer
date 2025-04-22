package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

class ReadBytes {

    private final byte[] data;

    ReadBytes(AssetManager assets, String filename, int size) {
        data = new byte[size];

        DataInputStream stream;
        try {
            stream = new DataInputStream(new BufferedInputStream(assets.open(filename)));
        } catch (IOException ignored) {
            throw new RuntimeException("Error opening asset: " + filename);
        }
        try {
            for (int i = 0; i < size; ++i) {
                byte b = stream.readByte();
                data[i] = b;
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

    byte[] get() {
        return data;
    }
}
