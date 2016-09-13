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

    static void reportFatalError(String error) {
        OrbitalApplication.tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(error)
                .setFatal(true)
                .build());
    }
}
