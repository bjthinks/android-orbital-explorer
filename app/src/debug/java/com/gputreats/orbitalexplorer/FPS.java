package com.gputreats.orbitalexplorer;

import android.util.Log;

final class FPS {
    private FPS() {}

    private static long lastFPSTimeMillis = 0L;
    private static int framesSinceLastFPS = 0;
    static void frame() {
        long now = System.currentTimeMillis();
        long millisBetweenRenders = now - lastFPSTimeMillis;
        if (millisBetweenRenders >= 1000L) {
            lastFPSTimeMillis = now;
            long fps4 = 10000 * framesSinceLastFPS / millisBetweenRenders;
            Log.d("FPS", Float.toString(fps4 / 10.0f));
            framesSinceLastFPS = 0;
        }
        ++framesSinceLastFPS;
    }
}
