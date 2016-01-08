package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;
import java.nio.FloatBuffer;

public class Integrate extends RenderStage {
    private static final String TAG = "Integrate";

    Integrate(Context context) {
    }

    public void newContext(AssetManager assetManager) {
        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "integrate.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "integrate.frag", GLES30.GL_FRAGMENT_SHADER);
        int mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        try {
            GLES30.glLinkProgram(mProgram);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        getGLError();
    }

    public void resize(int width, int height) {
    }

    public void render(float[] shaderTransform) {
    }
}
