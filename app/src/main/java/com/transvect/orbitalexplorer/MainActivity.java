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

        // Inflate a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        setContentView(R.layout.activity_main);

        mOrbitalView = (OrbitalView) findViewById(R.id.orbitalview);
        // Make an OrbitalRenderer. Needs assets for shader code.
        OrbitalRenderer renderer = new OrbitalRenderer(mOrbitalView, this);
        // Start the rendering thread
        mOrbitalView.setRenderer(renderer);
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
