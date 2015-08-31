package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by bwj on 8/31/15.
 */
public class RenderStage {

    private static final String TAG = "RenderStage";

    private int mProgram;
    private FloatBuffer mVertexBuffer;

    RenderStage() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f,
                1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);
    }

    public void newContext(AssetManager assetManager) {
        Shader vertexShader = new Shader(assetManager, "vertex.glsl", GLES20.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "fragment.glsl", GLES20.GL_FRAGMENT_SHADER);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader.getId());
        GLES20.glAttachShader(mProgram, fragmentShader.getId());
        GLES20.glLinkProgram(mProgram);
        getGLError();
    }

    public void render(float[] shaderTransform) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "shaderTransform");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);
        int inPositionHandle = GLES20.glGetAttribLocation(mProgram, "inPosition");
        GLES20.glEnableVertexAttribArray(inPositionHandle);
        GLES20.glVertexAttribPointer(inPositionHandle, 2, GLES20.GL_FLOAT, false, 8, mVertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

    private FloatBuffer floatArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    private void getGLError() {
        int error;
        while ((error = GLES20.glGetError()) != 0)
            Log.e(TAG, "OpenGL error code " + error);
    }
}
