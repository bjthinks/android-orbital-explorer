package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class RenderPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    int mColorMode;

    RenderPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mColorMode = Integer.parseInt(preferences.getString("prefColor", "0"));
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    synchronized int getColorMode() {
        return mColorMode;
    }

    @Override
    synchronized public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                       String key) {
        if (key.equals("prefColor"))
            mColorMode = Integer.parseInt(sharedPreferences.getString("prefColor", "0"));
    }
}
