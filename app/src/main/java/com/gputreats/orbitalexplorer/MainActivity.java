package com.gputreats.orbitalexplorer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity
        implements RenderStateProvider, ControlToggler, Handler.Callback {

    private RenderState renderState;
    private Toolbar toolbar;
    private OrbitalSelector orbitalSelector;
    private OrbitalView orbitalView;
    private boolean controlVisibility = true;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (!hasGLES30()) {
            // TODO show a helpful message
            throw new UnsupportedOperationException();
        }

        // Need to set renderState before calling setContentView, because that will
        // inflate an OrbitalView, which will ask its context (i.e. this object) for
        // the renderState.
        if (savedState != null) {
            controlVisibility = savedState.getBoolean(CONTROL_VISIBILITY_KEY);
            renderState = savedState.getParcelable(RENDER_STATE_KEY);
        } else {
            renderState = new RenderState();
        }

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        orbitalSelector = (OrbitalSelector) findViewById(R.id.orbitalselector);
        orbitalView = (OrbitalView) findViewById(R.id.orbitalview);

        setSupportActionBar(toolbar);

        /* // Can we use a translucent status bar?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // API 19+, yay!

            // Adjust toolbar downward so it doesn't overlap the Status Bar
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

            // Adjust menu as well
            menu.setPadding(0, getStatusBarHeight(), 0, 0);
        } */

        if (savedState != null)
            applyControlVisibility();
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

    @Override
    public RenderState provideRenderState() {
        return renderState;
    }

    private static final String CONTROL_VISIBILITY_KEY = "controlVisibility";
    private static final String RENDER_STATE_KEY = "renderState";

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(CONTROL_VISIBILITY_KEY, controlVisibility);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.menuShare:
                renderState.requestScreenGrab(new Handler(this));
                break;

            case R.id.menuColor:
                renderState.toggleColor();
                break;

            case R.id.menuAbout:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url", "file:///android_asset/docs/about.html");
                intent.putExtra("title", getString(R.string.menuAbout));
                startActivity(intent);
                break;

            case R.id.menuHelp:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra("url", "file:///android_asset/docs/help.html");
                intent.putExtra("title", getString(R.string.menuHelp));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message message) {
        int width = message.arg1;
        int height = message.arg2;
        int[] colors = new int[width * height];
        byte[] imageArray = ((ByteBuffer) message.obj).array();
        for (int row = 0; row < height; ++row) {
            for (int col = 0; col < width; ++col) {
                int cell = row * width + col;
                colors[(height - 1 - row) * width + col] = 0xff000000
                        | ((imageArray[4 * cell] & 0xff) << 16)
                        | ((imageArray[4 * cell + 1] & 0xff) << 8)
                        | (imageArray[4 * cell + 2] & 0xff);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);

        int name = (int) (System.currentTimeMillis() % 0x10000);
        File file = new File(getCacheDir(), "screens/" + name + ".jpg");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdirs())
                // TODO show toast
                return true;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ee) {
                // TODO show toast
                return true;
            }
        }
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
            // TODO show toast
            return true;
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            // TODO show toast
            return true;
        }

        Uri shareUri = FileProvider
                .getUriForFile(this, "com.gputreats.orbitalexplorer.provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, shareUri);
        intent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(intent, "Share image with");
        startActivityForResult(chooser, name);

        return true;
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        File file = new File(getCacheDir(), "screens/" + request + ".jpg");
        file.delete();
    }

    public void toggleControls() {
        controlVisibility = !controlVisibility;
        applyControlVisibility();
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
}
