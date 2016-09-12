package com.gputreats.orbitalexplorer;

import android.app.Application;

public class OrbitalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new OrbitalExceptionHandler(this));
    }
}
