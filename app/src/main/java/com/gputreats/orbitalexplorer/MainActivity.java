package com.gputreats.orbitalexplorer;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements RenderStateProvider {

    private RenderState renderState;
    private View decorView;
    private Toolbar toolbar;
    private OrbitalSelector orbitalSelector;
    private OrbitalView orbitalView;
    private boolean fullScreenMode;

    @Override
    public RenderState provideRenderState() {
        return renderState;
    }

    //
    // STARTUP -- CHECK OPENGL ES 3.0
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasGLES30())
            startApp();
        else
            showDriverError();
    }

    private boolean hasGLES30() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        int majorVersion = info.reqGlEsVersion >> 16;
        return majorVersion >= 3;
    }

    private void showDriverError() {
        setContentView(R.layout.activity_error);
    }

    //
    // INITIALIZATION
    //

    private void startApp() {

        // If savedState != null, can use savedState.getParcelable(String key)

        // Need to set renderState before calling setContentView, because that will
        // inflate an OrbitalView, which will ask its context (i.e. this object) for
        // the renderState.

        renderState = new RenderState();

        setContentView(R.layout.activity_main);
        decorView = getWindow().getDecorView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        orbitalSelector = (OrbitalSelector) findViewById(R.id.orbitalselector);
        orbitalView = (OrbitalView) findViewById(R.id.orbitalview);

        orbitalView.setOnSingleTapUp(() -> setFullscreen(false));

        decorView.setOnSystemUiVisibilityChangeListener((int flags) -> {
            // If we're in fullscreen mode and the decor has been shown, get the user out
            // of fullscreen mode. This tends to happen in two different ways:
            // (1) Swipe down from top, the built-in way to exit immersive fullscreen.
            //     We are not notified if this gesture is detected, but we do get
            //     an event here after re-displaying the decor.
            // (2) The user executes an immersive fullscreen "panic", by hitting the
            //     power button twice in five seconds,
            //     This causes the decor to be force-shown, but due to an apparent Android
            //     framework bug, it also causes the fullscreen state of the UI to get
            //     into an inconsistent state. The best we can do is to follow along and
            //     also show the app controls.
            if (fullScreenMode && (flags & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                setFullscreen(false);
        });

        setSupportActionBar(toolbar);

        setFullscreen(false);
    }

    //
    // LIFE CYCLE
    //

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Use outState.putParcelable(String key, T value) here
        super.onSaveInstanceState(outState);
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

    //
    // FULL SCREEN MODE
    //

    private void setFullscreen(boolean f) {
        if (f) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE);
            toolbar.setVisibility(View.INVISIBLE);
            orbitalSelector.setVisibility(View.INVISIBLE);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            toolbar.setVisibility(View.VISIBLE);
            orbitalSelector.setVisibility(View.VISIBLE);
        }
        decorView.requestLayout();
        fullScreenMode = f;
    }

    //
    // OPTIONS MENU
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.menuFullscreen:
                setFullscreen(true);
                break;

            case R.id.menuStore:
                DialogFragment confirm = new StoreConfirm();
                confirm.show(getFragmentManager(), "StoreConfirm");
                break;

            case R.id.menuAbout:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url",     "file:///android_asset/docs/about.html");
                intent.putExtra("url-v19", "file:///android_asset/docs/about.html");
                intent.putExtra("title", getString(R.string.menuAbout));
                startActivity(intent);
                break;

            case R.id.menuHelp:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url",     "file:///android_asset/docs/help.html");
                intent.putExtra("url-v19", "file:///android_asset/docs/help-v19.html");
                intent.putExtra("title", getString(R.string.menuHelp));
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    void gotoPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
