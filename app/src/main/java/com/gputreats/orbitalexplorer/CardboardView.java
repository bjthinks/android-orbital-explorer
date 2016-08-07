package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.google.vr.sdk.base.GvrView;

public class CardboardView extends GvrView {

    public CardboardView(Context context) {
        super(context);
        setup();
    }

    public CardboardView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setup();
    }

    void setup() {
        setRenderer(new CardboardRenderer());
    }
}
