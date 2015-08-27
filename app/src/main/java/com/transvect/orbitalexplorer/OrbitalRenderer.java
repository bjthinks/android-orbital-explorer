package com.transvect.orbitalexplorer;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The OpenGL guts start here.
 */

public class OrbitalRenderer extends MyGLRenderer {

    private static final String TAG = "OrbitalRenderer";

    private FloatBuffer vertexBuffer;
    private int mProgram;

    private final String vertexShaderSource
            = "attribute vec2 inPosition;"
            + "varying vec4 position;"
            + "void main() {"
            + "  position = vec4(inPosition.xy, 0, 1);"
            + "  gl_Position = position;"
            + "}";
    private final String fragmentShaderSource
            = "precision mediump float;"
            + "varying vec4 position;"
            + "void main() {"
            + "  gl_FragColor = vec4((position.xy + 1.0) / 2.0, (2.0 - position.x - position.y) / 4.0, 1);"
            + "}";

    private int loadShader(int type, String shaderSource) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        String result = GLES20.glGetShaderInfoLog(shader);
        if (!result.equals("")) {
            Log.e(TAG, "Shader failed to compile");
            Log.e(TAG, result);
        }
        return shader;
    }

    OrbitalRenderer() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f,
                 1.0f, -1.0f,
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoordinates);
        vertexBuffer.position(0);
    }

    @Override
    public void onCreate(int width, int height, boolean contextIsNew) {
        if (contextIsNew) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            getGLError();
        }
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        int inPositionHandle = GLES20.glGetAttribLocation(mProgram, "inPosition");
        GLES20.glEnableVertexAttribArray(inPositionHandle);
        GLES20.glVertexAttribPointer(inPositionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(inPositionHandle);
        getGLError();
    }

    private void getGLError() {
        int error;
        while ((error = GLES20.glGetError()) != 0)
            Log.e(TAG, "OpenGL error code " + error);
    }
}
