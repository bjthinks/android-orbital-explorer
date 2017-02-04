package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

enum MyGL {
    ;
    static void checkGLES() {
        int error = GLES30.glGetError();
        if (error != 0)
            throw new OpenGLException(error);
    }
}
