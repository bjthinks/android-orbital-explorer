package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;

import java.nio.FloatBuffer;

public class AxesDrawer {

    final FloatBuffer axes;
    private final AssetManager assets;

    AxesDrawer(Context context) {
        float[] axesCoordinates = {
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f
        };
        axes = FloatBufferFactory.make(axesCoordinates);
        assets = context.getAssets();
    }

    public void onSurfaceCreated() {
    }

    public void render(OrbitalData orbitalData /* Does this have maxRadius?*/, float[] transform) {
    }
}
