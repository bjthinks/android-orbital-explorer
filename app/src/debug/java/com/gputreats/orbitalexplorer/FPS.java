package com.gputreats.orbitalexplorer;

import android.util.Log;

final class FPS {

    private long lastFPSTimeMillis;
    private int framesSinceLastFPS;

    FPS() {
        lastFPSTimeMillis = System.currentTimeMillis();
        framesSinceLastFPS = 0;
    }

    void frame() {
        ++framesSinceLastFPS;
        long now = System.currentTimeMillis();
        long millisBetweenRenders = now - lastFPSTimeMillis;
        if (millisBetweenRenders >= 1000L) {
            lastFPSTimeMillis = now;
            long fps4 = 10000 * framesSinceLastFPS / millisBetweenRenders;
            Log.d("FPS", Float.toString(fps4 / 10.0f));
            framesSinceLastFPS = 0;
        }
    }
}
