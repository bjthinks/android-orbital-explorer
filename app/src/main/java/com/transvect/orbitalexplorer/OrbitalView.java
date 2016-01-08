package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class OrbitalView extends GLSurfaceView {
    public OrbitalView(Context context) {
        super(context);
        setEGLContextClientVersion(3);
    }
    public OrbitalView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setEGLContextClientVersion(3);
    }
}
