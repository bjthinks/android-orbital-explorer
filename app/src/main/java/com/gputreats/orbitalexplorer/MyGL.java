package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

final class MyGL {

    private MyGL() {}

    static void checkGLES() throws OpenGLException {
        int error = GLES30.glGetError();
        if (error != 0)
            throw new OpenGLException(error);
    }

    // Misc data manipulations for creating textures

    static FloatBuffer floatArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    static float[] functionToBuffer2(Function f, double start, double end, int N) {
        float data[] = new float[2 * N];
        double stepSize = (end - start) / N;
        double x = start;
        float value = (float) f.eval(x);
        for (int i = 0; i < N; ++i) {
            data[2 * i] = value;
            x += stepSize;
            value = (float) f.eval(x);
            data[2 * i + 1] = value;
        }
        return data;
    }
}
