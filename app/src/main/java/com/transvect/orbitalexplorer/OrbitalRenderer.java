package com.transvect.orbitalexplorer;

import android.opengl.GLES20;

/**
 * The OpenGL guts start here.
 */

public class OrbitalRenderer extends MyGLRenderer {
    @Override
    public void onCreate(int width, int height, boolean contextIsNew) {
        if (contextIsNew) {
            GLES20.glClearColor(1.0f, 0.5f, 0.0f, 1.0f);
        }
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
