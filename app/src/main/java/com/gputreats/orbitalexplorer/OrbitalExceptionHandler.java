package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.Intent;

class OrbitalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;

    OrbitalExceptionHandler(Context inContext) {
        context = inContext;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        reportException(throwable);
        startErrorActivity();
        System.exit(1);
    }

    private static void reportException(Throwable exception) {
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
        Analytics.reportFatalError(traceStr.toString());
    }

    private void startErrorActivity() {
        Intent intent = new Intent();
        intent.setClass(context, ErrorActivity.class);
        intent.setAction(ErrorActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
