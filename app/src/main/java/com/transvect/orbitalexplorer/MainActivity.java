package com.transvect.orbitalexplorer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
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
        mOrbitalView = (OrbitalView) findViewById(R.id.orbitalview);
        // Make an OrbitalRenderer. Needs assets for shader code.
        OrbitalRenderer renderer = new OrbitalRenderer(mOrbitalView, getAssets());
        // Start the rendering thread
        mOrbitalView.setRenderer(renderer);

        // new OrthogonalPolynomials(new WeightFunction());
        SymmetricTridiagonalMatrix M = new SymmetricTridiagonalMatrix(5);
        M.setDiagonal(0, 5);
        M.setDiagonal(1, 4);
        M.setDiagonal(2, 7);
        M.setDiagonal(3, 6);
        M.setDiagonal(4, 8);
        M.setOffDiagonal(0, 1);
        M.setOffDiagonal(1, 1.5);
        M.setOffDiagonal(2, 0.75);
        M.setOffDiagonal(3, 1.25);
        for (int i = 0; i < 1000; ++i)
            M.QRReduce();
        M.print();
    }
    private class WeightFunction implements Function {
        public double eval(double x) {
            return Math.exp(-Math.abs(x));
        }
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
