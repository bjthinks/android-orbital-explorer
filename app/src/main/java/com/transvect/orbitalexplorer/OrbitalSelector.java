package com.transvect.orbitalexplorer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class OrbitalSelector extends LinearLayout {

    private static final int maxN = 8;

    RenderState renderState;

    private int N;
    private int L;
    private int M;
    private boolean real;

    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;
    private Button rcChanger;

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
        try {
            renderState = ((RenderStateProvider) context).provideRenderState();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RenderStateProvider");
        }
        Orbital previouslyDisplayedOrbital = renderState.getOrbital();
        N = previouslyDisplayedOrbital.N;
        L = previouslyDisplayedOrbital.L;
        M = previouslyDisplayedOrbital.M;
        real = previouslyDisplayedOrbital.real;

        setOrientation(HORIZONTAL);
        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_orbitalselector, this);
        requestLayout();
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
        setReal(real);

        nChanger.setOnUpListener(new OnClickListener() {
            public void onClick(View v) {
                increaseN();
                orbitalChanged();
            }
        });
        nChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) {
                decreaseN();
                orbitalChanged();
            }
        });
        lChanger.setOnUpListener(new OnClickListener() {
            public void onClick(View v) {
                increaseL();
                orbitalChanged();
            }
        });
        lChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) {
                decreaseL();
                orbitalChanged();
            }
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
                setReal(!real);
                orbitalChanged();
            }
        });
    }

    public void setOrbital(int N_, int L_, int M_, boolean real_) {
        N = N_;
        L = L_;
        M = M_;
        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        setReal(real_);
        orbitalChanged();
    }

    private void orbitalChanged() {
        renderState.setOrbital(new Orbital(N, N, L, M, real));
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

    private void setReal(boolean realOrbital_) {
        real = realOrbital_;
        if (real)
            rcChanger.setText(R.string.realNumbers);
        else
            rcChanger.setText(R.string.complexNumbers);
    }
}
