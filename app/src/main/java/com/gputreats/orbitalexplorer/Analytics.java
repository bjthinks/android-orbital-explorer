package com.gputreats.orbitalexplorer;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public final class Analytics {

    private Analytics() {}

    private static Tracker tracker;
    public static void init(Context context) {
        tracker = GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker);
    }

    public static Tracker getTracker() {
        return tracker;
    }

    public static void reportException(Throwable exception) {
        String traceStr = exception.toString();
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < 3 && i < stackTrace.length; ++i) {
            traceStr += " ";
            StackTraceElement level = stackTrace[i];
            traceStr += level.getFileName() + ":" + level.getLineNumber();
        }

        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(traceStr)
                .setFatal(true)
                .build());
    }
}
