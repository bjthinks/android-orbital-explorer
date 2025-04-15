package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean enableColor;
    int colorBlind;

    public AppPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        enableColor = preferences.getBoolean("prefEnableColor", true);
        colorBlind = Integer.parseInt(preferences.getString("prefColorBlind", "0"));
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public synchronized boolean getEnableColor() {
        return enableColor;
    }

    public synchronized int getColorBlind() {
        return colorBlind;
    }

    @Override
    public synchronized void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("prefEnableColor"))
            enableColor = preferences.getBoolean("prefEnableColor", true);
        else if (key.equals("prefColorBlind"))
            colorBlind = Integer.parseInt(preferences.getString("prefColorBlind", "0"));
    }
}
