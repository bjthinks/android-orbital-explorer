package com.gputreats.orbitalexplorer;

import android.util.Log;

final class FPS {

    private long lastFPSTimeMillis;
    private long framesSinceLastFPS;

    FPS() {
        lastFPSTimeMillis = System.currentTimeMillis();
        framesSinceLastFPS = 0L;
    }

    void frame() {
        ++framesSinceLastFPS;
        long now = System.currentTimeMillis();
        long millisBetweenRenders = now - lastFPSTimeMillis;
        if (millisBetweenRenders >= 1000L) {
            lastFPSTimeMillis = now;
            long fps10 = 10000L * framesSinceLastFPS / millisBetweenRenders;
            Log.d("FPS", Float.toString((float) fps10 / 10.0f));
            framesSinceLastFPS = 0L;
        }
    }
}
