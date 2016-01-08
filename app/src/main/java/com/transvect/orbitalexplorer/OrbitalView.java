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
    // private static final String TAG = "OrbitalView";

    private Camera mCamera;
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

        mCamera = new Camera();
        mFlingDetector = new GestureDetector(context, new FlingListener());
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
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        return mCamera.computeShaderTransform(aspectRatio);
    }
}
