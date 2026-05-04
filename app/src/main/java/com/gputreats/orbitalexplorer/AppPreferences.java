package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.util.Objects;

public class AppPreferences
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean ultraQuality;
    boolean showAxes;
    int colorBlind;

    public AppPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ultraQuality = preferences.getBoolean("prefUltraQuality", false);
        showAxes = preferences.getBoolean("prefShowAxes", true);
        colorBlind = Integer.parseInt(preferences.getString("prefColorBlind", "0"));
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public synchronized boolean getUltraQuality() {
        return ultraQuality;
    }

    public synchronized boolean getShowAxes() {
        return showAxes;
    }

    public synchronized int getColorBlind() {
        return colorBlind;
    }

    @Override
    public synchronized void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        switch (Objects.requireNonNull(key)) {
            case "prefUltraQuality" ->
                    ultraQuality = preferences.getBoolean("prefUltraQuality", false);
            case "prefShowAxes" ->
                    showAxes = preferences.getBoolean("prefShowAxes", true);
            case "prefColorBlind" ->
                    colorBlind = Integer.parseInt(preferences.getString("prefColorBlind", "0"));
        }
    }
}
