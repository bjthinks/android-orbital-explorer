package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.Intent;

class OrbitalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;

    OrbitalExceptionHandler(Context c) {
        context = c;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Analytics.reportException(ex);

        Intent intent = new Intent();
        intent.setClass(context, ErrorActivity.class);
        intent.setAction(ErrorActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        System.exit(1);
    }
}
