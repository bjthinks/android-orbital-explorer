package com.transvect.orbitalexplorer;

import android.opengl.GLES30;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RenderStage {

    private static final String TAG = "RenderStage";

    FloatBuffer screenRectangle;

    RenderStage() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f,
                1.0f, -1.0f,
        };
        screenRectangle = floatArrayToBuffer(squareCoordinates);
    }

    protected static FloatBuffer floatArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    /* protected static float[] functionToBuffer(Function f, double start, double end, int steps) {
        float data[] = new float[steps + 1];
        for (int i = 0; i <= steps; ++i) {
            double x = start + (end - start) * (double) i / steps;
            data[i] = (float) f.eval(x);
        }
        return data;
    } */

    protected static float[] functionToBuffer2(Function f, double start, double end, int steps) {
        float data[] = new float[2 * (steps + 1)];
        for (int i = 0; i <= steps; ++i) {
            double x = start + (end - start) * (double) i / steps;
            data[2 * i] = (float) f.eval(x);
            x = start + (end - start) * (double) (i + 1) / steps;
            data[2 * i + 1] = (float) f.eval(x);
        }
        return data;
    }

    protected void setTexture2DMinMagFilters(int minFilter, int magFilter) {
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, magFilter);
    }

    protected static void getGLError() {
        int error;
        while ((error = GLES30.glGetError()) != 0)
            Log.e(TAG, "OpenGL error code " + error);
    }
}
