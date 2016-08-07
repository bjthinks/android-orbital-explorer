package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

public final class MyGL {

    private MyGL() {}

    protected static void checkGLES() throws OpenGLException {
        int error = GLES30.glGetError();
        if (error != 0)
            throw new OpenGLException(error);
    }
}
