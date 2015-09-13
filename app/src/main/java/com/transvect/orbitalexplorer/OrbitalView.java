package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class OrbitalView extends GLSurfaceView {
    private static final String TAG = "OrbitalView";

    private GestureDetector mFlingDetector;

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

        mFlingDetector = new GestureDetector(context, new FlingListener());
    }

    private Controller mController;

    public void setController(Controller controller) {
        mController = controller;
    }

    private int mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
    private int mSecondPointerID = MotionEvent.INVALID_POINTER_ID;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {

        mFlingDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // One bear in the bed
                mFirstPointerID = e.getPointerId(0);
                oneFingerEvent(e, false);
                mController.stopFling();
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
                // One falling out but at least one will remain
                // Which bear is falling out?
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
                // They all fell out, maybe because someone broke into the
                // bears' house and frightened them. They're hiding under
                // the bed and will come back out later when it's safe.
                mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
                mSecondPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            default:
                break;
        }

        return true;
    }

    private double mPreviousX;
    private double mPreviousY;

    private void oneFingerEvent(MotionEvent e, boolean actionable) {

        int pointerIndex = e.findPointerIndex(mFirstPointerID);

        double x = e.getX(pointerIndex);
        double y = e.getY(pointerIndex);

        if (actionable) {
            double dx = x - mPreviousX;
            double dy = y - mPreviousY;
            double meanSize = Math.sqrt(getWidth() * getHeight());
            mController.drag(dx / meanSize, dy / meanSize);

            requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
    }

    private double mPreviousDistance;
    private double mPreviousAngle;

    private void twoFingerEvent(MotionEvent e, boolean actionable) {

        int firstPointerIndex  = e.findPointerIndex(mFirstPointerID);
        int secondPointerIndex = e.findPointerIndex(mSecondPointerID);

        double x1 = e.getX(firstPointerIndex);
        double y1 = e.getY(firstPointerIndex);
        double x2 = e.getX(secondPointerIndex);
        double y2 = e.getY(secondPointerIndex);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        // Zero is highly unlikely, but don't take chances
        if (distance < 1.0)
            distance = 1.0;

        if (actionable) {

            double angleDifference = angle - mPreviousAngle;
            mController.spin(angleDifference);

            double zoomFactor = distance / mPreviousDistance;
            mController.zoom(zoomFactor);

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
            mController.fling(velocityX, velocityY);
            return true;
        }
    }
}
