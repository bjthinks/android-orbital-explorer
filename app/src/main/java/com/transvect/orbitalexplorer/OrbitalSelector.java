package com.transvect.orbitalexplorer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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
    private int Q = 1;
    private boolean realOrbital = false;

    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;
    private ValueChanger qChanger;
    private Button rcChanger;

    private OrbitalView listener;

    private boolean visibility = false;

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
        setVisibility(View.INVISIBLE);
        requestLayout();
    }

    @Override
    protected synchronized Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.N = N;
        ss.L = L;
        ss.M = M;
        ss.Q = Q;
        ss.realOrbital = realOrbital;

        return ss;
    }

    @Override
    protected synchronized void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        N = ss.N;
        L = ss.L;
        M = ss.M;
        Q = ss.Q;
        realOrbital = ss.realOrbital;

        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        qChanger.setInteger(Q);
        setRealOrbital(realOrbital);

        orbitalChanged();
    }

    private static class SavedState extends BaseSavedState {
        private int N, L, M, Q;
        private boolean realOrbital;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            N = in.readInt();
            L = in.readInt();
            M = in.readInt();
            realOrbital = (in.readInt() != 0);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(N);
            out.writeInt(L);
            out.writeInt(M);
            out.writeInt(realOrbital ? 1 : 0);
        }

        private static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        nChanger = (ValueChanger) findViewById(R.id.nchanger);
        lChanger = (ValueChanger) findViewById(R.id.lchanger);
        mChanger = (ValueChanger) findViewById(R.id.mchanger);
        qChanger = (ValueChanger) findViewById(R.id.qchanger);
        rcChanger = (Button) findViewById(R.id.rcchanger);

        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        qChanger.setInteger(Q);
        setRealOrbital(realOrbital);

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
        qChanger.setOnUpListener(new OnClickListener() {
            public void onClick(View v) {
                increaseQ();
                orbitalChanged();
            }
        });
        qChanger.setOnDownListener(new OnClickListener() {
            public void onClick(View v) {
                decreaseQ();
                orbitalChanged();
            }
        });

        rcChanger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRealOrbital(!realOrbital);
            }
        });
    }

    private void setRealOrbital(boolean realOrbital_) {
        realOrbital = realOrbital_;
        if (realOrbital)
            rcChanger.setText(R.string.realNumbers);
        else
            rcChanger.setText(R.string.complexNumbers);
        if (listener != null)
            listener.realFlagChanged(realOrbital);
    }

    public void setListener(OrbitalView orbitalView) {
        listener = orbitalView;
    }

    private void orbitalChanged() {
        if (listener != null) {
            /* Old logic for determining Q:
            int difficulty1 = N - L;      // radial nodes make rendering hard
            int difficulty2 = L - M + 1;  // azimuthal nodes make rendering hard
            int greaterDifficulty = Math.max(difficulty1, difficulty2);
            int lesserDifficulty  = Math.min(difficulty1, difficulty2);

            // The below formula comes from tons of experimentation and seems to give
            // eye-accurate renderings of all complex orbitals.
            int Q = greaterDifficulty + lesserDifficulty / 2 + M / 3; */

            listener.orbitalChanged(new Orbital(N, N, L, M, Q));
        }
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

    private void increaseQ() {
        if (Q < maxN) {
            ++Q;
            qChanger.setInteger(Q);
        }
    }

    private void decreaseQ() {
        if (Q > 1) {
            --Q;
            qChanger.setInteger(Q);
        }
    }

    public void setOrbital(int N_, int L_, int M_, int Q_) {
        N = N_;
        L = L_;
        M = M_;
        Q = Q_;
        nChanger.setInteger(N);
        lChanger.setInteger(L);
        mChanger.setInteger(M);
        qChanger.setInteger(Q);
        orbitalChanged();
    }

    public void toggleVisibility() {
        visibility = !visibility;
        if (visibility)
            setVisibility(View.VISIBLE);
        else
            setVisibility(View.INVISIBLE);
        requestLayout();
    }
}
