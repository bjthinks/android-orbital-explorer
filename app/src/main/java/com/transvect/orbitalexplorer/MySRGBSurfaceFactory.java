package com.transvect.orbitalexplorer;

import android.opengl.GLSurfaceView;
import android.util.Log;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class MySRGBSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {
    private static final String TAG = "MySRGBSurfaceFactory";

    @Override
    public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display,
                                          EGLConfig config, Object nativeWindow) {
        Log.d(TAG, "EGL extensions:" + egl.eglQueryString(display, EGL10.EGL_EXTENSIONS));

        Log.d(TAG, "Trying to request an sRGB default framebuffer");
        EGLSurface result = null;
        try {
            int[] attribs = new int[3];
            attribs[0] = 0x3087; // EGL_COLORSPACE
            attribs[1] = 0x3089; // EGL_COLORSPACE_sRGB (default, LINEAR, is 308a)
            attribs[2] = 0x3038; // EGL_NONE
            result = egl.eglCreateWindowSurface(display, config, nativeWindow, attribs);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "eglCreateWindowSurface", e);
        }
        if (result == null)
            Log.e(TAG, "Could not request sRGB default framebuffer");
        else
            Log.d(TAG, "Request succeeded");
        return result;
    }

    @Override
    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
        egl.eglDestroySurface(display, surface);
    }
}
