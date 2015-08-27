package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by bwj on 8/25/15.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        setup();
    }

    public MyGLSurfaceView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setup();
    }

    private void setup() {
        // Specify OpenGL ES version 2.0
        setEGLContextClientVersion(2);

        // Try to preserve our context, if possible
        setPreserveEGLContextOnPause(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
