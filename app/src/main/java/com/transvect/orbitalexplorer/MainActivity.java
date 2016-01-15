package com.transvect.orbitalexplorer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // private static final String TAG = "MainActivity";

    private OrbitalView mOrbitalView;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (!hasGLES30()) {
            // TODO show a helpful message
            throw new UnsupportedOperationException();
        }

        // Inflate a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        setContentView(R.layout.activity_main);

        // Find the toolbar and menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView menu = (ListView) findViewById(R.id.main_menu);

        // Set toolbar properties
        toolbar.setTitle("Orbital Explorer");

        // Can we use a translucent status bar?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // API 19+, yay!

            // Adjust toolbar downward so it doesn't overlap the Status Bar
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

            // Adjust menu as well
            menu.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        // Set the toolbar as the ActionBar for this Activity
        setSupportActionBar(toolbar);

        // Find the orbital control
        // OrbitalSelector orbitalSelector = (OrbitalSelector) findViewById(R.id.orbitalselector);

        mOrbitalView = (OrbitalView) findViewById(R.id.orbitalview);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    private boolean hasGLES30() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        int majorVersion = info.reqGlEsVersion >> 16;
        return majorVersion >= 3;
    }

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrbitalView != null)
            mOrbitalView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrbitalView != null)
            mOrbitalView.onResume();
    }
}
