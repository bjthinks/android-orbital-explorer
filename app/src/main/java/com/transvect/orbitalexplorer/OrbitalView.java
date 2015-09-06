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

        Log.d(TAG, "Pointer count = " + e.getPointerCount());

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG, "Down");
                int pointerIndex = e.getActionIndex();
                double x = e.getX(pointerIndex);
                double y = e.getY(pointerIndex);
                mPreviousX = x;
                mPreviousY = y;
                mFirstPointerID = e.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (e.getPointerCount() == 1) {
                    int pointerIndex = e.findPointerIndex(mFirstPointerID);
                    double x = e.getX(pointerIndex);
                    double y = e.getY(pointerIndex);
                    double dx = x - mPreviousX;
                    double dy = y - mPreviousY;
                    mPreviousX = x;
                    mPreviousY = y;

                    double rotx = Math.PI * dx / getWidth(); // TODO replace with getMeanSize()
                    double roty = Math.PI * dy / getHeight();
                    Quaternion xz_rotation = Quaternion.rotation(rotx, new Vector3(0, 1, 0));
                    Quaternion yz_rotation = Quaternion.rotation(roty, new Vector3(-1, 0, 0));
                    Quaternion increment = yz_rotation.multiply(xz_rotation);
                    mRenderer.rotateBy(increment);
                    requestRender();
                } else if (e.getPointerCount() == 2)
                    twoFingerEvent(e, true);
                break;
            }

            case MotionEvent.ACTION_UP:
                mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "Cancel");
                mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "Pointer Down");
                // A new pointer was added -- make it be second if we need one
                if (e.getPointerCount() == 2) {
                    mSecondPointerID = e.getPointerId(e.getActionIndex());
                    twoFingerEvent(e, false);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                Log.d(TAG, "Pointer Up");
                // An old pointer went away.
                int pointerIndex = e.getActionIndex();
                int pointerId = e.getPointerId(pointerIndex);
                // If it was the first one, make second be the new first. Swap them.
                if (pointerId == mFirstPointerID) {
                    mFirstPointerID = mSecondPointerID;
                    mSecondPointerID = pointerId;
                }
                // Save the (possibly new) first pointer's position
                int firstPointerIndex = e.findPointerIndex(mFirstPointerID);
                mPreviousX = e.getX(firstPointerIndex);
                mPreviousY = e.getY(firstPointerIndex);
                // Now, did we get rid of the second pointer?
                if (pointerId == mSecondPointerID) {
                    int newPointerIndex = 0;
                    while (newPointerIndex < e.getPointerCount()
                            && (e.getPointerId(newPointerIndex) == mFirstPointerID
                            || e.getPointerId(newPointerIndex) == mSecondPointerID))
                        ++newPointerIndex;
                    if (newPointerIndex < e.getPointerCount()) {
                        mSecondPointerID = e.getPointerId(newPointerIndex);
                        twoFingerEvent(e, false);
                    } else {
                        mSecondPointerID = MotionEvent.INVALID_POINTER_ID;
                    }
                }
                if (e.getPointerCount() > 2)
                    twoFingerEvent(e, false);
                break;
            }

            default:
                break;
        }

        return true;
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
