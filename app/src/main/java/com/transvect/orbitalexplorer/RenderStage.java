package com.transvect.orbitalexplorer;

import android.opengl.GLES30;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by bwj on 8/31/15.
 */
public class RenderStage {
    private static final String TAG = "DemoRenderStage";

    protected FloatBuffer floatArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    protected void getGLError() {
        int error;
        while ((error = GLES30.glGetError()) != 0)
            Log.e(TAG, "OpenGL error code " + error);
    }
}
