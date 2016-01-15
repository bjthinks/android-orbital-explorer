package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrbitalSelector extends LinearLayout {

    private static final String TAG = "OrbitalSelector";

    private IntegerChanger nChanger;
    private IntegerChanger lChanger;
    private IntegerChanger mChanger;

    public OrbitalSelector(Context context) {
        super(context);
        constructorSetup(context);
    }

    public OrbitalSelector(Context context, AttributeSet attribs) {
        super(context, attribs);
        constructorSetup(context);
    }

    public OrbitalSelector(Context context, AttributeSet attribs, int defStyle) {
        super(context, attribs, defStyle);
        constructorSetup(context);
    }

    private void constructorSetup(Context context) {
        setOrientation(HORIZONTAL);
        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.orbitalselector_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        nChanger = (IntegerChanger) findViewById(R.id.nchanger);
        lChanger = (IntegerChanger) findViewById(R.id.lchanger);
        mChanger = (IntegerChanger) findViewById(R.id.mchanger);

        nChanger.setRange(1, 8);
        lChanger.setRange(0, 0);
        mChanger.setRange(0, 0);

        nChanger.setOnChangeListener(new NChanged());
        lChanger.setOnChangeListener(new LChanged());
        mChanger.setOnChangeListener(new MChanged());
    }

    private class NChanged implements OnChangeListener {
        @Override
        public void onChange() {
            int newN = nChanger.getValue();
            Log.d(TAG, "N changed to " + newN);
            lChanger.setRange(0, newN - 1);
        }
    }

    private class LChanged implements OnChangeListener {
        @Override
        public void onChange() {
            int newL = lChanger.getValue();
            Log.d(TAG, "L changed to " + newL);
            mChanger.setRange(-newL, newL);
        }
    }

    private class MChanged implements OnChangeListener {
        @Override
        public void onChange() {
            int newM = mChanger.getValue();
            Log.d(TAG, "M changed to " + newM);
        }
    }
}
