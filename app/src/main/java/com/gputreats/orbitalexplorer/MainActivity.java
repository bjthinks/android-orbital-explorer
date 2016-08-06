package com.gputreats.orbitalexplorer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity implements RenderStateProvider {

    private RenderState renderState;
    private View decorView;
    private Toolbar toolbar;
    private OrbitalSelector orbitalSelector;
    private OrbitalView orbitalView;
    private boolean fullScreenMode = false;

    @Override
    public RenderState provideRenderState() {
        return renderState;
    }

    //
    // STARTUP -- CHECK OPENGL ES 3.0
    //

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (hasGLES30())
            startApp(savedState);
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
        Analytics.reportFatalError("GLES version check failed");
    }

    //
    // INITIALIZATION
    //

    private void startApp(Bundle savedState) {

        // Need to set renderState before calling setContentView, because that will
        // inflate an OrbitalView, which will ask its context (i.e. this object) for
        // the renderState.

        if (savedState != null) {
            renderState = savedState.getParcelable(RENDER_STATE_KEY);
        } else {
            renderState = new RenderState();
        }

        renderState.setRenderExceptionHandler(new Handler(new RenderExceptionCallback()));

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

    private static final String RENDER_STATE_KEY = "renderState";

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RENDER_STATE_KEY, renderState);
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

        Analytics.setScreenName("Main");
    }

    //
    // RENDER THREAD ERROR HANDLING
    //

    private boolean renderExceptionAlreadyReported = false;
    private class RenderExceptionCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message m) {
            if (!renderExceptionAlreadyReported) {
                renderExceptionAlreadyReported = true;
                throw (RuntimeException) m.obj;
            }
            return true;
        }
    }

    //
    // FULL SCREEN MODE
    //

    public void setFullscreen(boolean f) {
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

            case R.id.menuSnap:
                Analytics.reportEvent("menu", "snap");
                renderState.snapCameraToAxis();
                break;

            case R.id.menuFullscreen:
                Analytics.reportEvent("menu", "full");
                setFullscreen(true);
                break;

            case R.id.menuShare:
                Analytics.reportEvent("menu", "share");
                renderState.requestScreenGrab(new Handler(new ShareCallback(this)));
                break;

            case R.id.menuCardboard:
                intent = new Intent(this, CardboardActivity.class);
                startActivity(intent);
                break;

            case R.id.menuStore:
                Analytics.reportEvent("menu", "store");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.gputreats.orbitalexplorer"));
                startActivity(intent);
                break;

            case R.id.menuAbout:
                Analytics.reportEvent("menu", "about");
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url",     "file:///android_asset/docs/about.html");
                intent.putExtra("url-v19", "file:///android_asset/docs/about.html");
                intent.putExtra("title", getString(R.string.menuAbout));
                startActivity(intent);
                break;

            case R.id.menuHelp:
                Analytics.reportEvent("menu", "help");
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url",     "file:///android_asset/docs/help.html");
                intent.putExtra("url-v19", "file:///android_asset/docs/help-v19.html");
                intent.putExtra("title", getString(R.string.menuHelp));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        File file = new File(getCacheDir(), "screens/" + request + ".jpg");
        // TODO fix lint error
        file.delete();
    }
}
