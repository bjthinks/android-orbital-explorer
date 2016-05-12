package com.gputreats.orbitalexplorer;

import android.app.Application;
import android.content.Intent;

public class OrbitalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Analytics.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                handleUncaughtException(thread, ex);
            }
        });
    }

    public void handleUncaughtException(Thread thread, Throwable ex) {

        Analytics.reportException(ex);

        Intent intent = new Intent();
        intent.setAction("com.gputreats.orbitalexplorer.SHOW_ERROR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        System.exit(1);
    }
}
