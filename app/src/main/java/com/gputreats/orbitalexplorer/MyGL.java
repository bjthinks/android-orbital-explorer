package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

enum MyGL {
    ;

    static void checkGLES() {
        int error = GLES30.glGetError();
        if (error != 0)
            throw new OpenGLException(error);
    }

    // Misc data manipulations for creating textures

    static FloatBuffer floatArrayToBuffer(float[] array) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer fb = byteBuffer.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }
}
