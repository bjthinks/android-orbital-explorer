package com.gputreats.orbitalexplorer;

import android.util.Log;

public final class FPS {
    private FPS() {}

    static private long lastFPSTimeMillis = 0;
    static private int framesSinceLastFPS = 0;
    public static void frame() {
        long now = System.currentTimeMillis();
        long millisBetweenRenders = now - lastFPSTimeMillis;
        if (millisBetweenRenders >= 1000) {
            lastFPSTimeMillis = now;
            long fps4 = 4000 * framesSinceLastFPS / millisBetweenRenders;
            Log.d("FPS", Float.toString(fps4 / 4.0f));
            framesSinceLastFPS = 0;
        }
        ++framesSinceLastFPS;
    }
}
