package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class OrbitalSelector extends LinearLayout {

    private static final String TAG = "OrbitalSelector";

    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;

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
        inflater.inflate(R.layout.view_orbitalselector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        nChanger = (ValueChanger) findViewById(R.id.nchanger);
        lChanger = (ValueChanger) findViewById(R.id.lchanger);
        mChanger = (ValueChanger) findViewById(R.id.mchanger);

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
