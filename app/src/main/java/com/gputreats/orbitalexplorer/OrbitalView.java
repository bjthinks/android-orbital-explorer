package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class OrbitalView extends GLSurfaceView {

    private GestureDetector tapFlingDetector;
    private Camera camera;

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

        tapFlingDetector = new GestureDetector(context, new TapFlingListener());

        RenderState renderState = ((RenderStateProvider) context).provideRenderState();
        renderState.setOrbitalView(this);
        camera = ((RenderStateProvider) context).provideCamera();

        // Start the rendering thread
        setRenderer(new OrbitalRenderer(context, this));

        if (!renderState.orbital.color) {
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            requestRender();
        }
    }

    private Runnable onSingleTapUp;
    void setOnSingleTapUp(Runnable r) { onSingleTapUp = r; }

    private int firstPointerID = MotionEvent.INVALID_POINTER_ID;
    private int secondPointerID = MotionEvent.INVALID_POINTER_ID;

    private boolean stoppedFling;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        tapFlingDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // One bear in the bed
                firstPointerID = event.getPointerId(0);
                oneFingerEvent(event, false);
                stoppedFling = camera.stopFling();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // Two or more bears in the bed
                if (event.getPointerCount() == 2) {
                    secondPointerID = event.getPointerId(event.getActionIndex());
                    twoFingerEvent(event, false);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1)
                    oneFingerEvent(event, true);
                else if (event.getPointerCount() == 2)
                    twoFingerEvent(event, true);
                break;

            case MotionEvent.ACTION_UP:
                // No bears in the bed
                firstPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // One falling out but at least one will remain
                // Which bear is falling out?
                int goneIndex = event.getActionIndex();

                if (event.getPointerCount() == 3) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    firstPointerID = event.getPointerId(remainingIndex++);
                    if (remainingIndex == goneIndex) remainingIndex++;
                    secondPointerID = event.getPointerId(remainingIndex);
                    twoFingerEvent(event, false);
                } else if (event.getPointerCount() == 2) {
                    // So they all rolled over and one fell out
                    int remainingIndex = 0;
                    if (remainingIndex == goneIndex) remainingIndex++;
                    firstPointerID = event.getPointerId(remainingIndex);
                    secondPointerID = MotionEvent.INVALID_POINTER_ID;
                    oneFingerEvent(event, false);
                }

                break;

            case MotionEvent.ACTION_CANCEL:
                // They all fell out, maybe because someone broke into the
                // bears' house and frightened them. They're hiding under
                // the bed and will come back out later when it's safe.
                firstPointerID = MotionEvent.INVALID_POINTER_ID;
                secondPointerID = MotionEvent.INVALID_POINTER_ID;
                break;

            default:
                break;
        }

        return true;
    }

    private double previousX;
    private double previousY;

    private void oneFingerEvent(MotionEvent event, boolean actionable) {

        int pointerIndex = event.findPointerIndex(firstPointerID);

        double x = (double) event.getX(pointerIndex);
        double y = (double) event.getY(pointerIndex);

        if (actionable) {
            double meanSize = Math.sqrt((double) (getWidth() * getHeight()));
            double dx = (x - previousX) / meanSize;
            double dy = (y - previousY) / meanSize;
            camera.drag(dx, dy);
            requestRender();
        }

        previousX = x;
        previousY = y;
    }

    private double previousDistance;
    private double previousAngle;

    private void twoFingerEvent(MotionEvent event, boolean actionable) {

        int firstPointerIndex  = event.findPointerIndex(firstPointerID);
        int secondPointerIndex = event.findPointerIndex(secondPointerID);

        double x1 = (double) event.getX(firstPointerIndex);
        double y1 = (double) event.getY(firstPointerIndex);
        double x2 = (double) event.getX(secondPointerIndex);
        double y2 = (double) event.getY(secondPointerIndex);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        // Zero is highly unlikely, but don't take chances
        if (distance < 1.0) {
            angle = 0.0;
            distance = 1.0;
        }

        if (actionable) {

            double angleDifference = angle - previousAngle;
            camera.twist(angleDifference);
            requestRender();

            double zoomFactor = distance / previousDistance;
            camera.zoom(zoomFactor);
            requestRender();
        }

        previousAngle = angle;
        previousDistance = distance;
    }

    private class TapFlingListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if (!stoppedFling && onSingleTapUp != null)
                onSingleTapUp.run();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            camera.stopFling();
            camera.snapToAxis();
            requestRender();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            double meanSize = Math.sqrt((double) (getWidth() * getHeight()));
            camera.fling((double) velocityX / meanSize, (double) velocityY / meanSize);
            requestRender();

            return true;
        }
    }
}
