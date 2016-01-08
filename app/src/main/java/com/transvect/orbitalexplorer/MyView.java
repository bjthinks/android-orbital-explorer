package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyView extends GLSurfaceView {
    public MyView(Context context) {
        super(context);
        setEGLContextClientVersion(3);
    }
    public MyView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setEGLContextClientVersion(3);
    }
}
