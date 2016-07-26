package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrbitalSelector extends LinearLayout {

    private static final int maxN = 8;

    RenderState renderState;

    private Context context;
    private String plusMinus, minusPlus, realNumbers, complexNumbers;

    private int N;
    private int L;
    private int M;
    private boolean real;
    private boolean color;

    private TextView orbitalName;
    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;
    private Button rcChanger;
    private ImageButton colorChanger;

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

    private void constructorSetup(Context context_) {
        context = context_;

        plusMinus = context.getString(R.string.plusMinus);
        minusPlus = context.getString(R.string.minusPlus);
        realNumbers = context.getString(R.string.realNumbers);
        complexNumbers = context.getString(R.string.complexNumbers);

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
        color = previouslyDisplayedOrbital.color;

        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_orbitalselector, this);
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        orbitalName = (TextView) findViewById(R.id.orbitalname);
        nChanger = (ValueChanger) findViewById(R.id.nchanger);
        lChanger = (ValueChanger) findViewById(R.id.lchanger);
        mChanger = (ValueChanger) findViewById(R.id.mchanger);
        rcChanger = (Button) findViewById(R.id.rcchanger);
        colorChanger = (ImageButton) findViewById(R.id.colorchanger);

        setOrbitalName();
        nChanger.setInteger(N);
        lChanger.setInteger(L);
        setMChanger();
        setReal(real);
        setColor(color);
        setButtonTint();

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
                setMChanger();
                orbitalChanged();
            }
        });
        colorChanger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(!color);
                orbitalChanged();
            }
        });
    }

    private void orbitalChanged() {
        setOrbitalName();
        setButtonTint();
        renderState.setOrbital(1, N, L, M, real, color);
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
            setMChanger();
            if (M > L)
                increaseL();
        }
    }

    private void decreaseM() {
        if (M > 1 - maxN) {
            --M;
            setMChanger();
            if (M < -L)
                increaseL();
        }
    }

    private void setMChanger() {
        if (real && M > 0)
            mChanger.setText(plusMinus + M);
        else if (real && M < 0)
            mChanger.setText(minusPlus + (-M));
        else
            mChanger.setInteger(M);
    }

    private void setReal(boolean realOrbital_) {
        real = realOrbital_;
        if (real)
            rcChanger.setText(realNumbers);
        else
            rcChanger.setText(complexNumbers);
    }

    private void setColor(boolean newColor) {
        color = newColor;
        int d;
        if (color)
            d = R.drawable.ic_palette_white_24dp;
        else
            d = R.drawable.bnw;
        colorChanger.setImageDrawable(ContextCompat.getDrawable(context, d));
    }

    private void setButtonTint() {
        int dark = Color.rgb(0, 0, 0);
        int dim = Color.rgb(128, 128, 128);
        int bright = Color.rgb(255, 255, 255);

        if (N == maxN)
            nChanger.setUpTint(dark);
        else
            nChanger.setUpTint(bright);

        if (N == 1)
            nChanger.setDownTint(dark);
        else if (N <= L + 1)
            nChanger.setDownTint(dim);
        else
            nChanger.setDownTint(bright);

        if (L == maxN - 1)
            lChanger.setUpTint(dark);
        else if (L >= N - 1)
            lChanger.setUpTint(dim);
        else
            lChanger.setUpTint(bright);

        if (L == 0)
            lChanger.setDownTint(dark);
        else if (L <= Math.abs(M))
            lChanger.setDownTint(dim);
        else
            lChanger.setDownTint(bright);

        if (M == maxN - 1)
            mChanger.setUpTint(dark);
        else if (M >= L)
            mChanger.setUpTint(dim);
        else
            mChanger.setUpTint(bright);

        if (M == 1 - maxN)
            mChanger.setDownTint(dark);
        else if (M <= -L)
            mChanger.setDownTint(dim);
        else
            mChanger.setDownTint(bright);
    }

    private void setOrbitalName() {
        String name = Integer.toString(N);
        String subscript = "";
        if (real) {
            if (M > 0) subscript = plusMinus;
            if (M < 0) subscript = minusPlus;
            subscript += Math.abs(M);
        } else {
            subscript = Integer.toString(M);
        }
        switch (L) {
            case 0:
                name += "s";
                subscript = "";
                break;
            case 1:
                name += "p";
                if (real) {
                    switch (M) {
                        case -1:
                            subscript = "y";
                            break;
                        case 0:
                            subscript = "z";
                            break;
                        case 1:
                            subscript = "x";
                            break;
                    }
                }
                break;
            case 2:
                name += "d";
                if (real) {
                    switch (M) {
                        case -2:
                            subscript = "xy";
                            break;
                        case -1:
                            subscript = "yz";
                            break;
                        case 0:
                            subscript = "z" + ss(2);
                            break;
                        case 1:
                            subscript = "xz";
                            break;
                        case 2:
                            subscript = "x" + ss(2) + "-y" + ss(2);
                            break;
                    }
                }
                break;
            case 3:
                name += "f";
                if (real) {
                    switch (M) {
                        case -3:
                            subscript = "y(3x" + ss(2) + "-y" + ss(2) + ")";
                            break;
                        case -2:
                            subscript = "xyz";
                            break;
                        case -1:
                            subscript = "yz" + ss(2);
                            break;
                        case 0:
                            subscript = "z" + ss(3);
                            break;
                        case 1:
                            subscript = "xz" + ss(2);
                            break;
                        case 2:
                            subscript = "z(x" + ss(2) + "-y" + ss(2) + ")";
                            break;
                        case 3:
                            subscript = "x(x" + ss(2) + "-3y" + ss(2) + ")";
                            break;
                    }
                }
                break;
            case 4:
                name += "g";
                if (real) {
                    switch (M) {
                        case -4:
                            subscript = "xy(x" + ss(2) + "-y" + ss(2) + ")";
                            break;
                        case -3:
                            subscript = "zy" + ss(3);
                            break;
                        case -2:
                            subscript = "z" + ss(2) + "xy";
                            break;
                        case -1:
                            subscript = "z" + ss(3) + "y";
                            break;
                        case 0:
                            subscript = "z" + ss(4);
                            break;
                        case 1:
                            subscript = "z" + ss(3) + "x";
                            break;
                        case 2:
                            subscript = "z" + ss(2) + "(x" + ss(2) + "-y" + ss(2) + ")";
                            break;
                        case 3:
                            subscript = "zx" + ss(3);
                            break;
                        case 4:
                            subscript = "x" + ss(4) + "+y" + ss(4);
                            break;
                    }
                }
                break;
            case 5:
                name += "h";
                break;
            case 6:
                name += "i";
                break;
            case 7:
                name += "k";
                break;
            default:
                name += Integer.toString(L);
        }
        name += "<sub>" + subscript + "</sub>";
        orbitalName.setText(Html.fromHtml(name));
    }

    private static String ss(int x) {
        return "<sup><small>" + Integer.toString(x) + "</small></sup>";
    }
}
