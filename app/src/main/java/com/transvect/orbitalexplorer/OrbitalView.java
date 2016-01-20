package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

// TODO review concurrency
public class OrbitalView extends GLSurfaceView implements OrbitalChangedListener {
    private static final String TAG = "OrbitalView";

    private Camera mCamera;
    private GestureDetector mFlingDetector;
    private OrbitalRenderer orbitalRenderer;

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

        mCamera = new Camera();
        mFlingDetector = new GestureDetector(context, new FlingListener());

        // Start the rendering thread
        orbitalRenderer = new OrbitalRenderer(this, context);
        setRenderer(orbitalRenderer);
    }

    @Override
    protected synchronized Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.camera = mCamera;
        return ss;
    }

    @Override
    protected synchronized void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mCamera = ss.camera;
    }

    private static class SavedState extends BaseSavedState {
        Camera camera;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            camera = in.readParcelable(Camera.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(camera, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public void onOrbitalChanged(Orbital o) {
            orbitalRenderer.onOrbitalChanged(o);
    }

    private int mFirstPointerID = MotionEvent.INVALID_POINTER_ID;
    private int mSecondPointerID = MotionEvent.INVALID_POINTER_ID;

    @Override
    public synchronized boolean onTouchEvent(@NonNull MotionEvent e) {

        mFlingDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // One bear in the bed
                mFirstPointerID = e.getPointerId(0);
                oneFingerEvent(e, false);
                mCamera.stopFling();
                // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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

    private synchronized void oneFingerEvent(MotionEvent e, boolean actionable) {

        int pointerIndex = e.findPointerIndex(mFirstPointerID);

        double x = e.getX(pointerIndex);
        double y = e.getY(pointerIndex);

        if (actionable) {
            double dx = x - mPreviousX;
            double dy = y - mPreviousY;
            double meanSize = Math.sqrt(getWidth() * getHeight());
            mCamera.drag(dx / meanSize, dy / meanSize);

            requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
    }

    private double mPreviousDistance;
    private double mPreviousAngle;

    private synchronized void twoFingerEvent(MotionEvent e, boolean actionable) {

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
            mCamera.twist(angleDifference);

            double zoomFactor = distance / mPreviousDistance;
            mCamera.zoom(zoomFactor);

            requestRender();
        }

        mPreviousAngle = angle;
        mPreviousDistance = distance;
    }

    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        // private static final String TAG = "FlingListener";

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            synchronized (OrbitalView.this) {
                double meanSize = Math.sqrt(getWidth() * getHeight());
                mCamera.fling(velocityX / meanSize, velocityY / meanSize);
                setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }

            return true;
        }
    }

    // This function can be called by the rendering thread
    // Hence the need for "synchronized" all over the place
    public synchronized float[] getNextTransform(double aspectRatio) {
        if (!mCamera.continueFling())
            ; // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        return mCamera.computeShaderTransform(aspectRatio);
    }
}
