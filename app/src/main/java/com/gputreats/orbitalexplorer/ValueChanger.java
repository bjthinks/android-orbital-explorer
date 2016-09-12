package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class ValueChanger extends LinearLayout {

    private TextView text;
    private ImageButton upArrow;
    private ImageButton downArrow;

    public ValueChanger(Context context) {
        super(context);
        constructorSetup(context);
    }

    public ValueChanger(Context context, AttributeSet attribs) {
        super(context, attribs);
        constructorSetup(context);
    }

    public ValueChanger(Context context, AttributeSet attribs, int defStyle) {
        super(context, attribs, defStyle);
        constructorSetup(context);
    }

    private void constructorSetup(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_valuechanger, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        upArrow = (ImageButton) findViewById(R.id.integerchanger_uparrow);
        downArrow = (ImageButton) findViewById(R.id.integerchanger_downarrow);
        text = (TextView) findViewById(R.id.integerchanger_value);
    }

    void setText(CharSequence t) {
        text.setText(t);
    }

    void setInteger(int i) {
        text.setText(String.format(Locale.US, "%d", i));
    }

    void setUpTint(int c) {
        upArrow.setColorFilter(c);
    }

    void setDownTint(int c) {
        downArrow.setColorFilter(c);
    }

    void setOnUpListener(OnClickListener ocl) {
        upArrow.setOnClickListener(ocl);
    }

    void setOnDownListener(OnClickListener ocl) {
        downArrow.setOnClickListener(ocl);
    }
}
