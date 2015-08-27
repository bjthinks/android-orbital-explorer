package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A wrapper around GLSurfaceView that ensures we do proper setup.
 */

public class OrbitalView extends GLSurfaceView {

    OrbitalRenderer mRenderer;
    static float angle = 0;

    public OrbitalView(Context context) {
        super(context);
        setup();
    }

    public OrbitalView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setup();
    }

    private void setup() {
        // Specify OpenGL ES version 2.0
        setEGLContextClientVersion(2);

        // Try to preserve our context, if possible
        setPreserveEGLContextOnPause(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new OrbitalRenderer();
        mRenderer.setAngle(angle);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (y > getHeight() / 2)
                    dx = -dx;
                if (x < getWidth() / 2)
                    dy = -dy;
                angle += (dx + dy) * TOUCH_SCALE_FACTOR;
                mRenderer.setAngle(angle);
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
