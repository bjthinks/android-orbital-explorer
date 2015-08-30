package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A wrapper around GLSurfaceView that ensures we do proper setup
 * and handles input events.
 */

public class OrbitalView extends GLSurfaceView {
    private static final String TAG = "OrbitalView";

    OrbitalRenderer mRenderer;
    static Quaternion rotation = new Quaternion(1);

    public OrbitalView(Context context) {
        super(context);
        setup(context);
    }

    public OrbitalView(Context context, AttributeSet attribs) {
        super(context, attribs);
        setup(context);
    }

    private void setup(Context context) {
        // Specify OpenGL ES version 2.0
        setEGLContextClientVersion(2);

        // Try to preserve our context, if possible
        setPreserveEGLContextOnPause(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new OrbitalRenderer(context);
        mRenderer.setRotation(rotation.asRotationMatrix());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                double dx = x - mPreviousX;
                double dy = y - mPreviousY;
                double rotx = Math.PI * dx / getWidth();
                double roty = Math.PI * dy / getHeight();
                Quaternion xz_rotation = Quaternion.rotation(rotx, new Vector3( 0, 1, 0));
                Quaternion yz_rotation = Quaternion.rotation(roty, new Vector3(-1, 0, 0));
                rotation = xz_rotation.multiply(rotation);
                rotation = yz_rotation.multiply(rotation);
                rotation = rotation.multiply(1 / rotation.norm());
                mRenderer.setRotation(rotation.asRotationMatrix());
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
