package com.gputreats.orbitalexplorer;

import com.google.android.gms.analytics.HitBuilders;

enum Analytics {
    ;

    static void setScreenName(String screen) {
        OrbitalApplication.tracker.setScreenName(screen);
        OrbitalApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    static void reportEvent(String category, String action) {
        OrbitalApplication.tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    static void reportException(Throwable exception) {
        StringBuilder traceStr = new StringBuilder();
        traceStr.append(exception);
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < 3 && i < stackTrace.length; ++i) {
            traceStr.append(' ');
            StackTraceElement level = stackTrace[i];
            traceStr.append(level.getFileName());
            traceStr.append(':');
            traceStr.append(level.getLineNumber());
        }
        reportFatalError(traceStr.toString());
    }

    static void reportFatalError(String error) {
        OrbitalApplication.tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(error)
                .setFatal(true)
                .build());
    }
}
