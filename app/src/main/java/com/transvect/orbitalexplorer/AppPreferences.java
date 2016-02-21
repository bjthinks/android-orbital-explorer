package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean enableColor;

    public AppPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        enableColor = preferences.getBoolean("prefEnableColor", true);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public synchronized boolean getEnableColor() {
        return enableColor;
    }

    @Override
    public synchronized void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("prefEnableColor"))
            enableColor = preferences.getBoolean("prefEnableColor", true);
    }
}
