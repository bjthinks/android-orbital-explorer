package com.transvect.orbitalexplorer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private OrbitalSelector orbitalSelector;
    private OrbitalView orbitalView;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (!hasGLES30()) {
            // TODO show a helpful message
            throw new UnsupportedOperationException();
        }

        setContentView(R.layout.activity_main);
        toolbar         = (Toolbar)         findViewById(R.id.toolbar);
        orbitalSelector = (OrbitalSelector) findViewById(R.id.orbitalselector);
        orbitalView     = (OrbitalView)     findViewById(R.id.orbitalview);

        setSupportActionBar(toolbar);

        /* // Can we use a translucent status bar?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // API 19+, yay!

            // Adjust toolbar downward so it doesn't overlap the Status Bar
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

            // Adjust menu as well
            menu.setPadding(0, getStatusBarHeight(), 0, 0);
        } */

        orbitalView.setControlToggler(new VisibilityToggler());
        orbitalSelector.setOrbitalChanger(new OrbitalChanger());
        orbitalSelector.setOrbital(4, 2, 1, false);
    }

    /* private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);
        return result;
    } */

    private boolean hasGLES30() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        int majorVersion = info.reqGlEsVersion >> 16;
        return majorVersion >= 3;
    }

    private static final String CONTROL_VISIBILITY_KEY = "controlVisibility";

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(CONTROL_VISIBILITY_KEY, controlVisibility);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        controlVisibility = inState.getBoolean(CONTROL_VISIBILITY_KEY);
        super.onRestoreInstanceState(inState);
        applyControlVisibility();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orbitalView != null)
            orbitalView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orbitalView != null)
            orbitalView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {

            case R.id.menuAbout:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url", "file:///android_asset/about.html");
                intent.putExtra("title", "About");
                break;

            case R.id.menuSettings:
                intent = new Intent(this, SettingsActivity.class);
                break;

            case R.id.menuHelp:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url", "file:///android_asset/help.html");
                intent.putExtra("title", "Help");
                break;
        }
        if (intent != null)
            startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    private boolean controlVisibility = true;
    private class VisibilityToggler implements Listener {

        public void event() {
            controlVisibility = !controlVisibility;
            applyControlVisibility();
        }
    }

    private void applyControlVisibility() {
        if (controlVisibility) {
            toolbar.setVisibility(View.VISIBLE);
            orbitalSelector.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.INVISIBLE);
            orbitalSelector.setVisibility(View.INVISIBLE);
        }
    }

    private class OrbitalChanger implements Listener {
        public void event() {
            orbitalView.setOrbital(orbitalSelector.getOrbital());
        }
    }
}
