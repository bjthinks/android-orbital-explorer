package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class OrbitalSelector extends LinearLayout {

    private static final String TAG = "OrbitalSelector";

    private static final int maxN = 8;

    private int N = 1;
    private int L = 0;
    private int M = 0;

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

        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);

        nChanger.setOnUpListener(new NUp());
        nChanger.setOnDownListener(new NDown());
        lChanger.setOnUpListener(new LUp());
        lChanger.setOnDownListener(new LDown());
        mChanger.setOnUpListener(new MUp());
        mChanger.setOnDownListener(new MDown());
    }

    private void increaseN() {
        ++N;
        nChanger.setInteger(N);
    }

    private void decreaseN() {
        --N;
        nChanger.setInteger(N);
        if (L >= N)
            decreaseL();
    }

    private void increaseL() {
        ++L;
        lChanger.setInteger(L);
        if (L >= N)
            increaseN();
    }

    private void decreaseL() {
        --L;
        lChanger.setInteger(L);
        if (M > L)
            decreaseM();
        else if (M < -L)
            increaseM();
    }

    private void increaseM() {
        ++M;
        mChanger.setInteger(M);
        if (M > L)
            increaseL();
    }

    private void decreaseM() {
        --M;
        mChanger.setInteger(M);
        if (M < -L)
            increaseL();
    }

    private class NUp implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (N < maxN)
                increaseN();
        }
    }

    private class NDown implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (N > 0)
                decreaseN();
        }
    }

    private class LUp implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (L < maxN - 1)
                increaseL();
        }
    }

    private class LDown implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (L > 0)
                decreaseL();
        }
    }

    private class MUp implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (M < maxN - 1)
                increaseM();
        }
    }

    private class MDown implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (M > 1 - maxN)
                decreaseM();
        }
    }
}
