package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * A wrapper around GLSurfaceView that ensures we do proper setup
 * and handles input events.
 */

public class OrbitalView extends GLSurfaceView {
    private static final String TAG = "OrbitalView";

    OrbitalRenderer mRenderer;

    public OrbitalView(Context context) {
        super(context);
        constructorSetup(context);
    }

    public OrbitalView(Context context, AttributeSet attribs) {
        super(context, attribs);
        constructorSetup(context);
    }

    private void constructorSetup(Context context) {
        // Specify OpenGL ES version 3.0
        setEGLContextClientVersion(3);

        // Try to preserve our context, if possible
        setPreserveEGLContextOnPause(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new OrbitalRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mFlingDetector = new GestureDetector(context, new FlingListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    private float mPreviousX;
    private float mPreviousY;

    private GestureDetector mFlingDetector;
    private ScaleGestureDetector mScaleDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        mFlingDetector.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                int pointerIndex = e.getActionIndex();
                float x = e.getX(pointerIndex);
                float y = e.getY(pointerIndex);
                mPreviousX = x;
                mPreviousY = y;
                mActivePointerId = e.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = e.findPointerIndex(mActivePointerId);
                float x = e.getX(pointerIndex);
                float y = e.getY(pointerIndex);
                double dx = x - mPreviousX;
                double dy = y - mPreviousY;
                mPreviousX = x;
                mPreviousY = y;
                invalidate(); // TODO learn about invalidate vs requestRender

                if (!mScaleDetector.isInProgress()) {
                    double rotx = Math.PI * dx / getWidth(); // TODO replace with getMeanSize()
                    double roty = Math.PI * dy / getHeight();
                    Quaternion xz_rotation = Quaternion.rotation(rotx, new Vector3(0, 1, 0));
                    Quaternion yz_rotation = Quaternion.rotation(roty, new Vector3(-1, 0, 0));
                    Quaternion increment = yz_rotation.multiply(xz_rotation);
                    mRenderer.rotateBy(increment);
                    invalidate();
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // A new pointer was added, but the good one is still there
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                // An old pointer went away, was it the good one?
                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = (pointerIndex == 0 ? 1 : 0);
                    mPreviousX = e.getX(newPointerIndex);
                    mPreviousY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }

            default:
                break;
        }

        return true;
    }

    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "FlingListener";

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            // TODO implement flinging
            Log.d(TAG, "Fling: velocity = ( " + velocityX + " , " + velocityY + " )");
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mRenderer.scaleBy(detector.getScaleFactor());
            invalidate();
            return true;
        }
    }
}
