package com.transvect.orbitalexplorer;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Encapsulates proper lifecycle and maintenance tasks away from the
 * actual OpenGL drawing, which occurs in a derived class.
 */

// TODO marge this class with OrbitalRenderer and consult documentation
public abstract class MyGLRenderer implements GLSurfaceView.Renderer {

    private boolean mSurfaceIsNew;
    private int mWidth;
    private int mHeight;
    private long mLastTime;
    private int mFramesDrawnThisSecond;
    private int mFPS;

    public MyGLRenderer() {
        mSurfaceIsNew = true;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFramesDrawnThisSecond = 0;
        mFPS = 0;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mSurfaceIsNew = true;
        mWidth = -1;
        mHeight = -1;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if (!mSurfaceIsNew && width == mWidth && height == mHeight)
            return;

        mWidth = width;
        mHeight = height;

        onCreate(mWidth, mHeight, mSurfaceIsNew);
        mSurfaceIsNew = false;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        onDrawFrame();

        ++mFramesDrawnThisSecond;
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime >= 1000) {
            mFPS = mFramesDrawnThisSecond;
            mFramesDrawnThisSecond = 0;
            mLastTime = currentTime;
        }
    }

    public int getFPS() {
        return mFPS;
    }

    public abstract void onCreate(int width, int height, boolean contextIsNew);

    public abstract void onDrawFrame();
}
