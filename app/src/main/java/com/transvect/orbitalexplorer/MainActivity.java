package com.transvect.orbitalexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private OrbitalView mOrbitalView;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_main);

        mOrbitalView = (OrbitalView) findViewById(R.id.orbitalview);
        OrbitalRenderer renderer = new OrbitalRenderer(mOrbitalView, this);
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
