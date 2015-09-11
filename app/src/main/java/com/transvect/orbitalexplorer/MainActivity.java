package com.transvect.orbitalexplorer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    private OrbitalView mOrbitalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasGLES30()) {
            // TODO show a helpful message
            throw new UnsupportedOperationException();
        }

        // Inflate a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        setContentView(R.layout.activity_main);
        mOrbitalView = (OrbitalView) findViewById(R.id.orbitalview);

        // Encapsulate all communication with the render thread and all long-term state
        Controller controller = new Controller();

        // Make an OrbitalRenderer. Needs assets for shader code.
        OrbitalRenderer renderer = new OrbitalRenderer(controller, getAssets());

        // Link everything up and start rendering
        mOrbitalView.setController(controller);
        mOrbitalView.setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        mOrbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private boolean hasGLES30() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = manager.getDeviceConfigurationInfo();
        int majorVersion = info.reqGlEsVersion >> 16;
        return majorVersion >= 3;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
