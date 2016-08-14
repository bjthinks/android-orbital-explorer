package com.gputreats.orbitalexplorer;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

final class Analytics {

    private Analytics() {}

    private static Tracker tracker;
    static void init(Context context) {

        // For testing
        GoogleAnalytics.getInstance(context).setDryRun(true);

        tracker = GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker);
        tracker.enableAdvertisingIdCollection(true);
    }

    static void setScreenName(String screen) {
        tracker.setScreenName(screen);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    static void reportEvent(String category, String action) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    static void reportException(Throwable exception) {

        String traceStr = exception.toString();
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < 3 && i < stackTrace.length; ++i) {
            traceStr += " ";
            StackTraceElement level = stackTrace[i];
            traceStr += level.getFileName() + ":" + level.getLineNumber();
        }

        reportFatalError(traceStr);
    }

    static void reportFatalError(String error) {
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(error)
                .setFatal(true)
                .build());
    }
}
