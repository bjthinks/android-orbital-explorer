package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.util.Log;

public final class Analytics {

    private static final String TAG = "Analytics";

    private Analytics() {}

    public static void init(Context context) {
    }

    public static void setScreenName(String screen) {
        Log.d(TAG, "Screen: " + screen);
    }

    public static void reportEvent(String category, String action) {
        Log.d(TAG, "Event: " + category + ", " + action);
    }

    public static void reportException(Throwable exception) {

        String traceStr = exception.toString();
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < 3 && i < stackTrace.length; ++i) {
            traceStr += " ";
            StackTraceElement level = stackTrace[i];
            traceStr += level.getFileName() + ":" + level.getLineNumber();
        }

        reportFatalError(traceStr);
    }

    public static void reportFatalError(String error) {
        Log.d(TAG, "Fatal: " + error);
    }
}
