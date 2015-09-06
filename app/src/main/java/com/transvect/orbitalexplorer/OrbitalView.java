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
    }

    private int mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
    private int mSecondPointerID = MotionEvent.INVALID_POINTER_ID;
    private double mPreviousX;
    private double mPreviousY;
    private double mPreviousDistance;
    private double mPreviousAngle;

    private GestureDetector mFlingDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        mFlingDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // One bear in the bed
                mFirstPointerID = e.getPointerId(0);
                oneFingerEvent(e, false);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // Two or more bears in the bed
                if (e.getPointerCount() == 2) {
                    mSecondPointerID = e.getPointerId(e.getActionIndex());
                    twoFingerEvent(e, false);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() == 1)
                    oneFingerEvent(e, true);
                else if (e.getPointerCount() == 2)
                    twoFingerEvent(e, true);
                break;

            case MotionEvent.ACTION_UP:
                // No bears in the bed
                mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                // One fell out but some remain
                // Which bear fell out?
                int goneIndex = e.getActionIndex();

                if (e.getPointerCount() == 3) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    mFirstPointerID = e.getPointerId(remainingIndex++);
                    if (remainingIndex == goneIndex) remainingIndex++;
                    mSecondPointerID = e.getPointerId(remainingIndex);
                    twoFingerEvent(e, false);
                } else if (e.getPointerCount() == 2) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    mFirstPointerID = e.getPointerId(remainingIndex);
                    mSecondPointerID = MotionEvent.INVALID_POINTER_ID;
                    oneFingerEvent(e, false);
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
                // They all fell out
                mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
                mSecondPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            default:
                break;
        }

        return true;
    }

    private void oneFingerEvent(MotionEvent e, boolean actionable) {

        int pointerIndex = e.findPointerIndex(mFirstPointerID);

        double x = e.getX(pointerIndex);
        double y = e.getY(pointerIndex);

        if (actionable) {
            double dx = x - mPreviousX;
            double dy = y - mPreviousY;
            double rotx = Math.PI * dx / getWidth(); // TODO replace with getMeanSize()
            double roty = Math.PI * dy / getHeight();
            Quaternion xz_rotation = Quaternion.rotation(rotx, new Vector3(0, 1, 0));
            Quaternion yz_rotation = Quaternion.rotation(roty, new Vector3(-1, 0, 0));
            Quaternion composite = yz_rotation.multiply(xz_rotation);
            mRenderer.rotateBy(composite);

            requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
    }

    private void twoFingerEvent(MotionEvent e, boolean actionable) {

        int firstPointerIndex  = e.findPointerIndex(mFirstPointerID);
        int secondPointerIndex = e.findPointerIndex(mSecondPointerID);

        double x1 = e.getX(firstPointerIndex);
        double y1 = e.getY(firstPointerIndex);
        double x2 = e.getX(secondPointerIndex);
        double y2 = e.getY(secondPointerIndex);

        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (distance < 40.0)
            distance = 40.0;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        if (actionable) {

            double angleDifference = angle - mPreviousAngle;
            Quaternion xy_rotation = Quaternion.rotation(angleDifference, new Vector3(0, 0, 1));
            mRenderer.rotateBy(xy_rotation);

            double scaleFactor = distance / mPreviousDistance;
            mRenderer.scaleBy(scaleFactor);

            requestRender();
        }

        mPreviousAngle = angle;
        mPreviousDistance = distance;
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
}
