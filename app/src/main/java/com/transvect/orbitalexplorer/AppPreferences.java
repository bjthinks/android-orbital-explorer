package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean enableColor;
    boolean cycleColors;

    AppPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        enableColor = preferences.getBoolean("prefEnableColor", true);
        cycleColors = preferences.getBoolean("prefCycleColors", true);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    synchronized boolean getEnableColor() {
        return enableColor;
    }

    synchronized boolean getCycleColors() {
        return cycleColors;
    }

    @Override
    synchronized public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("prefEnableColor"))
            enableColor = preferences.getBoolean("prefEnableColor", true);
        if (key.equals("prefCycleColors"))
            cycleColors = preferences.getBoolean("prefCycleColors", true);
    }
}
