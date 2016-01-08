package com.transvect.orbitalexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
