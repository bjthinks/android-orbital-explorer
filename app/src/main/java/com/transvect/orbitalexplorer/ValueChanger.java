package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ValueChanger extends LinearLayout {

    private static final String TAG = "ValueChanger";

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

    public void setText(String t) {
        text.setText(t);
    }

    public void setInteger(int i) {
        text.setText(String.format("%d", i));
    }

    public void setOnUpListener(OnClickListener ocl) {
        upArrow.setOnClickListener(ocl);
    }

    public void setOnDownListener(OnClickListener ocl) {
        downArrow.setOnClickListener(ocl);
    }
}
