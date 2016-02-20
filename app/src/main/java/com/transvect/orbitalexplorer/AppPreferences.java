package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean enableColor;

    AppPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        enableColor = preferences.getBoolean("prefEnableColor", true);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    synchronized boolean getEnableColor() {
        return enableColor;
    }

    @Override
    synchronized public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("prefEnableColor"))
            enableColor = preferences.getBoolean("prefEnableColor", true);
    }
}
