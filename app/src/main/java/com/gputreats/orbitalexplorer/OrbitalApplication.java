package com.gputreats.orbitalexplorer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class OrbitalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                handleUncaughtException(thread, ex);
            }
        });
    }

    public void handleUncaughtException(Thread thread, Throwable ex) {

        String traceStr = ex.toString();
        StackTraceElement[] trace = ex.getStackTrace();
        for (int i = 0; i < 3 && i < trace.length; ++i) {
            traceStr += " ";
            StackTraceElement level = trace[i];
            traceStr += level.getFileName() + ":" + level.getLineNumber();
        }

        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(traceStr)
                .setFatal(true)
                .build());

        Intent intent = new Intent();
        intent.setAction("com.gputreats.orbitalexplorer.SHOW_ERROR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        System.exit(1);
    }

    private Tracker tracker;

    synchronized public Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

}
