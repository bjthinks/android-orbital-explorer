package com.gputreats.orbitalexplorer;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

class OrbitalApplication extends Application {

    static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if (BuildConfig.DEBUG)
                analytics.setDryRun(true);
            tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }
        Thread.setDefaultUncaughtExceptionHandler(new OrbitalExceptionHandler(this));
    }
}
