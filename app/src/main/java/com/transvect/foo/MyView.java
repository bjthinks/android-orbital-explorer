package com.transvect.foo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyView extends GLSurfaceView {
    MyRenderer renderer;
    public MyView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setEGLContextClientVersion(3);
        renderer = new MyRenderer();
        setRenderer(renderer);
    }
}
