package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

public final class MyGL {

    private MyGL() {}

    protected static void checkGLES() throws OpenGLException {
        int error = GLES30.glGetError();
        if (error != 0)
            throw new OpenGLException(error);
    }

    // A misc data manipulation
    protected static float[] functionToBuffer2(Function f, double start, double end, int N) {
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
