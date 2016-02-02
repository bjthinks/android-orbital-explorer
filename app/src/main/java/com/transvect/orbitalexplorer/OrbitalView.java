package com.transvect.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

// TODO review concurrency
public class OrbitalView extends GLSurfaceView {
    private static final String TAG = "OrbitalView";

    private Camera camera;
    private GestureDetector flingDetector;
    private OrbitalSelector orbitalSelector;
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

        camera = new Camera();
        flingDetector = new GestureDetector(context, new FlingListener());

        // Start the rendering thread
        orbitalRenderer = new OrbitalRenderer(this, context);
        setRenderer(orbitalRenderer);
    }

    @Override
    protected synchronized Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.camera = camera;
        return ss;
    }

    @Override
    protected synchronized void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        camera = ss.camera;
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

    public void setOrbitalSelector(OrbitalSelector s) {
        orbitalSelector = s;
    }

    public void orbitalChanged(Orbital newOrbital) {
        orbitalRenderer.orbitalChanged(newOrbital);
    }

    public void realFlagChanged(boolean realOrbital) {
        orbitalRenderer.realFlagChanged(realOrbital);
    }

    private int firstPointerID = MotionEvent.INVALID_POINTER_ID;
    private int secondPointerID = MotionEvent.INVALID_POINTER_ID;
    private boolean isTouchEventTrivial = false;

    @Override
    public synchronized boolean onTouchEvent(@NonNull MotionEvent e) {

        flingDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // One bear in the bed
                firstPointerID = e.getPointerId(0);
                oneFingerEvent(e, false);
                isTouchEventTrivial = !camera.stopFling();
                // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // Two or more bears in the bed
                if (e.getPointerCount() == 2) {
                    secondPointerID = e.getPointerId(e.getActionIndex());
                    twoFingerEvent(e, false);
                }
                isTouchEventTrivial = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() == 1)
                    oneFingerEvent(e, true);
                else if (e.getPointerCount() == 2)
                    twoFingerEvent(e, true);
                isTouchEventTrivial = false;
                break;

            case MotionEvent.ACTION_UP:
                // No bears in the bed
                firstPointerID = MotionEvent.INVALID_POINTER_ID;
                if (isTouchEventTrivial && orbitalSelector != null)
                    orbitalSelector.toggleVisibility();
                isTouchEventTrivial = false;
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                // One falling out but at least one will remain
                // Which bear is falling out?
                int goneIndex = e.getActionIndex();

                if (e.getPointerCount() == 3) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    firstPointerID = e.getPointerId(remainingIndex++);
                    if (remainingIndex == goneIndex) remainingIndex++;
                    secondPointerID = e.getPointerId(remainingIndex);
                    twoFingerEvent(e, false);
                } else if (e.getPointerCount() == 2) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    firstPointerID = e.getPointerId(remainingIndex);
                    secondPointerID = MotionEvent.INVALID_POINTER_ID;
                    oneFingerEvent(e, false);
                }
                isTouchEventTrivial = false;

                break;
            }

            case MotionEvent.ACTION_CANCEL:
                // They all fell out, maybe because someone broke into the
                // bears' house and frightened them. They're hiding under
                // the bed and will come back out later when it's safe.
                firstPointerID = MotionEvent.INVALID_POINTER_ID;
                secondPointerID = MotionEvent.INVALID_POINTER_ID;
                isTouchEventTrivial = false;
                break;

            default:
                break;
        }

        return true;
    }

    private double previousX;
    private double previousY;

    private synchronized void oneFingerEvent(MotionEvent e, boolean actionable) {

        int pointerIndex = e.findPointerIndex(firstPointerID);

        double x = e.getX(pointerIndex);
        double y = e.getY(pointerIndex);

        if (actionable) {
            double dx = x - previousX;
            double dy = y - previousY;
            double meanSize = Math.sqrt(getWidth() * getHeight());
            camera.drag(dx / meanSize, dy / meanSize);

            requestRender();
        }

        previousX = x;
        previousY = y;
    }

    private double previousDistance;
    private double previousAngle;

    private synchronized void twoFingerEvent(MotionEvent e, boolean actionable) {

        int firstPointerIndex  = e.findPointerIndex(firstPointerID);
        int secondPointerIndex = e.findPointerIndex(secondPointerID);

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

            double angleDifference = angle - previousAngle;
            camera.twist(angleDifference);

            double zoomFactor = distance / previousDistance;
            camera.zoom(zoomFactor);

            requestRender();
        }

        previousAngle = angle;
        previousDistance = distance;
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
                camera.fling(velocityX / meanSize, velocityY / meanSize);
                setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }

            return true;
        }
    }

    // This function can be called by the rendering thread
    // Hence the need for "synchronized" all over the place
    public synchronized float[] getNextTransform(double aspectRatio) {
        if (!camera.continueFling())
            ; // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        return camera.computeShaderTransform(aspectRatio);
    }
}
