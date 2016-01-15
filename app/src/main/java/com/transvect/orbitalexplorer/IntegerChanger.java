package com.transvect.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntegerChanger extends LinearLayout {

    private static final String TAG = "IntegerChanger";

    private ImageButton upArrow;
    private ImageButton downArrow;
    private TextView text;
    private Integer value;

    public IntegerChanger(Context context) {
        super(context);
        constructorSetup(context);
    }

    public IntegerChanger(Context context, AttributeSet attribs) {
        super(context, attribs);
        constructorSetup(context);
    }

    public IntegerChanger(Context context, AttributeSet attribs, int defStyle) {
        super(context, attribs, defStyle);
        constructorSetup(context);
    }

    private void constructorSetup(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.integerchanger_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        upArrow = (ImageButton) findViewById(R.id.integerchanger_uparrow);
        downArrow = (ImageButton) findViewById(R.id.integerchanger_downarrow);
        text = (TextView) findViewById(R.id.integerchanger_value);

        upArrow.setOnClickListener(new Modifier(1));
        downArrow.setOnClickListener(new Modifier(-1));

        setValue(0);
    }

    private class Modifier implements OnClickListener {
        private int delta;

        public Modifier(int d) {
            delta = d;
        }

        @Override
        public void onClick(View view) {
            setValue(value + delta);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        value = v;
        text.setText(value.toString());
    }
}
