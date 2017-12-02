package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrbitalSelector extends LinearLayout {

    private static final int COLOR_DARK = Color.rgb(0, 0, 0);
    private static final int COLOR_DIM = Color.rgb(128, 128, 128);
    private static final int COLOR_BRIGHT = Color.rgb(255, 255, 255);

    private OrbitalView orbitalView;

    private String plusMinus, minusPlus, realNumbers, complexNumbers;
    private Drawable drawableColor, drawableMono;
    private Drawable drawablePlay, drawablePaused;

    private int qN;
    private int qL;
    private int qM;
    private boolean real;
    private boolean color;
    private long pauseTime;

    private TextView orbitalName;
    private ValueChanger nChanger;
    private ValueChanger lChanger;
    private ValueChanger mChanger;
    private Button rcChanger;
    private ImageButton colorChanger;
    private ImageButton pauseChanger;

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
        plusMinus = context.getString(R.string.plusMinus);
        minusPlus = context.getString(R.string.minusPlus);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            realNumbers = context.getString(R.string.realNumbers);
            complexNumbers = context.getString(R.string.complexNumbers);
        } else {
            // The unicode glyphs appear to be missing pre-Lollipop
            realNumbers = "R";
            complexNumbers = "C";
        }

        drawableColor  = ContextCompat.getDrawable(context, R.drawable.ic_palette_white_24dp);
        drawableMono   = ContextCompat.getDrawable(context, R.drawable.bnw);
        drawablePlay   = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white_24dp);
        drawablePaused = ContextCompat.getDrawable(context, R.drawable.ic_pause_white_24dp);

        qN = 4;
        qL = 2;
        qM = 1;
        real = false;
        color = true;
        pauseTime = 0;

        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_orbitalselector, this);

        requestLayout();

        orbitalName = findViewById(R.id.orbitalname);
        nChanger = findViewById(R.id.nchanger);
        lChanger = findViewById(R.id.lchanger);
        mChanger = findViewById(R.id.mchanger);
        rcChanger = findViewById(R.id.rcchanger);
        colorChanger = findViewById(R.id.colorchanger);
        pauseChanger = findViewById(R.id.pausechanger);

        nChanger.setOnUpListener((View v) -> {
            increaseN();
            orbitalChanged();
        });
        nChanger.setOnDownListener((View v) -> {
            decreaseN();
            orbitalChanged();
        });
        lChanger.setOnUpListener((View x) -> {
            increaseL();
            orbitalChanged();
        });
        lChanger.setOnDownListener((View v) -> {
            decreaseL();
            orbitalChanged();
        });
        mChanger.setOnUpListener((View v) -> {
            increaseM();
            orbitalChanged();
        });
        mChanger.setOnDownListener((View v) -> {
            decreaseM();
            orbitalChanged();
        });
        rcChanger.setOnClickListener((View v) -> {
            real = !real;
            orbitalChanged();
        });
        colorChanger.setOnClickListener((View v) -> {
            color = !color;
            orbitalChanged();
        });
        pauseChanger.setOnClickListener((View v) -> {
            if (pauseTime != 0)
                pauseTime = 0;
            else
                pauseTime = System.currentTimeMillis();
            orbitalChanged();
        });

        orbitalChanged();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("qN", qN);
        bundle.putInt("qL", qL);
        bundle.putInt("qM", qM);
        bundle.putBoolean("real", real);
        bundle.putBoolean("color", color);
        bundle.putLong("pause", pauseTime);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("superState"));
        qN = bundle.getInt("qN");
        qL = bundle.getInt("qL");
        qM = bundle.getInt("qM");
        real = bundle.getBoolean("real");
        color = bundle.getBoolean("color");
        pauseTime = bundle.getLong("pauseTime");
        orbitalChanged();
    }

    private void increaseN() {
        if (qN < Orbital.MAX_N) {
            ++qN;
        }
    }

    private void decreaseN() {
        if (qN > 1) {
            --qN;
            if (qL >= qN)
                decreaseL();
        }
    }

    private void increaseL() {
        if (qL < Orbital.MAX_N - 1) {
            ++qL;
            if (qL >= qN)
                increaseN();
        }
    }

    private void decreaseL() {
        if (qL > 0) {
            --qL;
            if (qM > qL)
                decreaseM();
            else if (qM < -qL)
                increaseM();
        }
    }

    private void increaseM() {
        if (qM < Orbital.MAX_N - 1) {
            ++qM;
            if (qM > qL)
                increaseL();
        }
    }

    private void decreaseM() {
        if (qM > 1 - Orbital.MAX_N) {
            --qM;
            if (qM < -qL)
                increaseL();
        }
    }

    void setOrbitalView(OrbitalView ov) {
        orbitalView = ov;
        orbitalChanged();
    }

    private void orbitalChanged() {
        nChanger.setInteger(qN);
        lChanger.setInteger(qL);
        setMChanger();
        setReal();
        setColor();
        setPause();
        setButtonTint();
        setOrbitalName();

        if (orbitalView != null)
            orbitalView.onOrbitalChanged(new Orbital(1, qN, qL, qM, real, color));
    }

    private void setMChanger() {
        if (real && qM > 0)
            mChanger.setText(plusMinus + qM);
        else if (real && qM < 0)
            mChanger.setText(minusPlus + -qM);
        else
            mChanger.setInteger(qM);

        if (qM == 0)
            rcChanger.setTextColor(COLOR_DIM);
        else
            rcChanger.setTextColor(COLOR_BRIGHT);
    }

    private void setReal() {
        if (real)
            rcChanger.setText(realNumbers);
        else
            rcChanger.setText(complexNumbers);
    }

    private void setColor() {
        colorChanger.setImageDrawable(color ? drawableColor : drawableMono);
    }

    private void setPause() {
        pauseChanger.setImageDrawable(pauseTime == 0 ? drawablePlay : drawablePaused);
    }

    private void setButtonTint() {
        if (qN == Orbital.MAX_N)
            nChanger.setUpTint(COLOR_DARK);
        else
            nChanger.setUpTint(COLOR_BRIGHT);

        if (qN == 1)
            nChanger.setDownTint(COLOR_DARK);
        else if (qN <= qL + 1)
            nChanger.setDownTint(COLOR_DIM);
        else
            nChanger.setDownTint(COLOR_BRIGHT);

        if (qL == Orbital.MAX_N - 1)
            lChanger.setUpTint(COLOR_DARK);
        else if (qL >= qN - 1)
            lChanger.setUpTint(COLOR_DIM);
        else
            lChanger.setUpTint(COLOR_BRIGHT);

        if (qL == 0)
            lChanger.setDownTint(COLOR_DARK);
        else if (qL <= Math.abs(qM))
            lChanger.setDownTint(COLOR_DIM);
        else
            lChanger.setDownTint(COLOR_BRIGHT);

        if (qM == Orbital.MAX_N - 1)
            mChanger.setUpTint(COLOR_DARK);
        else if (qM >= qL)
            mChanger.setUpTint(COLOR_DIM);
        else
            mChanger.setUpTint(COLOR_BRIGHT);

        if (qM == 1 - Orbital.MAX_N)
            mChanger.setDownTint(COLOR_DARK);
        else if (qM <= -qL)
            mChanger.setDownTint(COLOR_DIM);
        else
            mChanger.setDownTint(COLOR_BRIGHT);
    }

    private void setOrbitalName() {
        String name = Integer.toString(qN);
        String subscript = "";
        if (real) {
            if (qM > 0) subscript = plusMinus;
            if (qM < 0) subscript = minusPlus;
            subscript += Math.abs(qM);
        } else {
            subscript = Integer.toString(qM);
        }
        switch (qL) {
            case 0:
                name += "s";
                subscript = "";
                break;
            case 1:
                name += "p";
                if (real) {
                    switch (qM) {
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
                    switch (qM) {
                        case -2:
                            subscript = "xy";
                            break;
                        case -1:
                            subscript = "yz";
                            break;
                        case 0:
                            subscript = 'z' + ss(2);
                            break;
                        case 1:
                            subscript = "xz";
                            break;
                        case 2:
                            subscript = 'x' + ss(2) + "-y" + ss(2);
                            break;
                    }
                }
                break;
            case 3:
                name += "f";
                if (real) {
                    switch (qM) {
                        case -3:
                            subscript = "y(3x" + ss(2) + "-y" + ss(2) + ')';
                            break;
                        case -2:
                            subscript = "xyz";
                            break;
                        case -1:
                            subscript = "yz" + ss(2);
                            break;
                        case 0:
                            subscript = 'z' + ss(3);
                            break;
                        case 1:
                            subscript = "xz" + ss(2);
                            break;
                        case 2:
                            subscript = "z(x" + ss(2) + "-y" + ss(2) + ')';
                            break;
                        case 3:
                            subscript = "x(x" + ss(2) + "-3y" + ss(2) + ')';
                            break;
                    }
                }
                break;
            case 4:
                name += "g";
                if (real) {
                    switch (qM) {
                        case -4:
                            subscript = "xy(x" + ss(2) + "-y" + ss(2) + ')';
                            break;
                        case -3:
                            subscript = "zy" + ss(3);
                            break;
                        case -2:
                            subscript = 'z' + ss(2) + "xy";
                            break;
                        case -1:
                            subscript = 'z' + ss(3) + 'y';
                            break;
                        case 0:
                            subscript = 'z' + ss(4);
                            break;
                        case 1:
                            subscript = 'z' + ss(3) + 'x';
                            break;
                        case 2:
                            subscript = 'z' + ss(2) + "(x" + ss(2) + "-y" + ss(2) + ')';
                            break;
                        case 3:
                            subscript = "zx" + ss(3);
                            break;
                        case 4:
                            subscript = 'x' + ss(4) + "+y" + ss(4);
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
                name += Integer.toString(qL);
        }
        name += "<sub>" + subscript + "</sub>";
        Spanned formattedName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formattedName = Html.fromHtml(name, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            formattedName = Html.fromHtml(name);
        }
        orbitalName.setText(formattedName);
    }

    private static String ss(int x) {
        return "<sup><small>" + Integer.toString(x) + "</small></sup>";
    }
}
