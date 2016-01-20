package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RenderPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    int colorMode;

    RenderPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        colorMode = Integer.parseInt(preferences.getString("prefColorMode", "0"));
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    synchronized int getColorMode() {
        return colorMode;
    }

    @Override
    synchronized public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("prefColorMode"))
            colorMode = Integer.parseInt(preferences.getString("prefColorMode", "0"));
    }
}
