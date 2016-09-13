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

    static float[] functionToBuffer2(Function f, double start, double end, int n) {
        float[] data = new float[2 * n];
        double stepSize = (end - start) / (double) n;
        double x = start;
        float value = (float) f.eval(x);
        for (int i = 0; i < n; ++i) {
            data[2 * i] = value;
            x += stepSize;
            value = (float) f.eval(x);
            data[2 * i + 1] = value;
        }
        return data;
    }
}
