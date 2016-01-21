package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class OrbitalSelector extends LinearLayout {

    private static final String TAG = "OrbitalSelector";

    private static final int maxN = 8;

    private int N = 1;
    private int L = 0;
    private int M = 0;
    private boolean realOrbital = false;

    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;
    private Button rcChanger;

    private OrbitalChangedListener orbitalChangedListener;

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
        rcChanger = (Button) findViewById(R.id.rcchanger);

        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        setRcChangerText();

        nChanger.setOnUpListener(  new OnClickListener() {
            public void onClick(View v) { increaseN(); orbitalChanged(); }
        });
        nChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) { decreaseN(); orbitalChanged(); }
        });
        lChanger.setOnUpListener(  new OnClickListener() {
            public void onClick(View v) { increaseL(); orbitalChanged(); }
        });
        lChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) { decreaseL(); orbitalChanged(); }
        });
        mChanger.setOnUpListener(new OnClickListener() {
            public void onClick(View v) {
                increaseM();
                orbitalChanged();
            }
        });
        mChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) {
                decreaseM();
                orbitalChanged();
            }
        });

        rcChanger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                realOrbital = !realOrbital;
                setRcChangerText();
            }
        });
    }

    private void setRcChangerText() {
        if (realOrbital)
            rcChanger.setText(R.string.realNumbers);
        else
            rcChanger.setText(R.string.complexNumbers);
    }

    public void setOrbitalChangedListener(OrbitalChangedListener listener) {
        orbitalChangedListener = listener;
    }

    private void orbitalChanged() {
        if (orbitalChangedListener != null)
            orbitalChangedListener.onOrbitalChanged(new Orbital(N, N, L, M));
    }

    private void increaseN() {
        if (N < maxN) {
            ++N;
            nChanger.setInteger(N);
        }
    }

    private void decreaseN() {
        if (N > 1) {
            --N;
            nChanger.setInteger(N);
            if (L >= N)
                decreaseL();
        }
    }

    private void increaseL() {
        if (L < maxN - 1) {
            ++L;
            lChanger.setInteger(L);
            if (L >= N)
                increaseN();
        }
    }

    private void decreaseL() {
        if (L > 0) {
            --L;
            lChanger.setInteger(L);
            if (M > L)
                decreaseM();
            else if (M < -L)
                increaseM();
        }
    }

    private void increaseM() {
        if (M < maxN - 1) {
            ++M;
            mChanger.setInteger(M);
            if (M > L)
                increaseL();
        }
    }

    private void decreaseM() {
        if (M > 1 - maxN) {
            --M;
            mChanger.setInteger(M);
            if (M < -L)
                increaseL();
        }
    }

    public void setOrbital(int N_, int L_, int M_) {
        Log.d(TAG, "SETTING ORBITAL TO " + N_ + " " + L_ + " " + M_);
        N = N_;
        L = L_;
        M = M_;
        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        orbitalChanged();
    }
}
