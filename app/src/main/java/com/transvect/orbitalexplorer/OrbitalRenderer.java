package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitalRenderer implements GLSurfaceView.Renderer {
    private AssetManager mAssetManager;

    // Main thread
    public OrbitalRenderer(OrbitalView orbitalView, Context context) {
        mAssetManager = context.getAssets();
    }

    // Rendering thread
    @Override public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Shader vertexShader = new Shader(mAssetManager, "integrate.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(mAssetManager, "integrate.frag", GLES30.GL_FRAGMENT_SHADER);
        int mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        try {
            GLES30.glLinkProgram(mProgram);
        } catch (Exception e) {
            Log.e("Renderer", e.getLocalizedMessage());
        }
    }
    @Override public void onSurfaceChanged(GL10 xx, int width, int height) {}
    @Override public void onDrawFrame(GL10 xx) {}
}
