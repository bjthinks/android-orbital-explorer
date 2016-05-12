package com.gputreats.orbitalexplorer;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
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
}
