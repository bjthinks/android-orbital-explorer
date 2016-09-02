package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.util.AttributeSet;

import com.google.vr.sdk.base.GvrView;

public class CardboardView extends GvrView {

    public CardboardView(Context context) {
        super(context);
        setup(context);
    }

    public CardboardView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setup(context);
    }

    void setup(Context context) {
        setEGLContextClientVersion(3);
        setRenderer(new CardboardRenderer(context));
    }
}
