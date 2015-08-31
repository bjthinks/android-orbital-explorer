package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The OpenGL guts start here.
 */

public class OrbitalRenderer extends MyGLRenderer {

    private static final String TAG = "OrbitalRenderer";

    private RenderStage mRenderStage;

    private FloatBuffer vertexBuffer;
    private int mProgram;

    private final float[] mProjectionMatrix = new float[16];

    private static Quaternion mThisFrameRotation = new Quaternion(1);
    private static Quaternion mRotationalMomentum = new Quaternion(1);
    private static Quaternion mTotalRotation = new Quaternion(1);

    public void rotateBy(Quaternion r)
    {
        if (mThisFrameRotation != null)
            mThisFrameRotation = r.multiply(mThisFrameRotation);
        else
            mThisFrameRotation = r;
    }

    private AssetManager assetManager;

    OrbitalRenderer(Context context) {
        assetManager = context.getAssets();
        mRenderStage = new RenderStage();
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
            Shader vertexShader = new Shader(assetManager, "vertex.glsl", GLES20.GL_VERTEX_SHADER);
            Shader fragmentShader = new Shader(assetManager, "fragment.glsl", GLES20.GL_FRAGMENT_SHADER);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader.getId());
            GLES20.glAttachShader(mProgram, fragmentShader.getId());
            GLES20.glLinkProgram(mProgram);
            getGLError();
        }
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) Math.sqrt((double) width / (double) height);
        float leftRight = ratio;
        float bottomTop = 1.0f / ratio;
        float near = 1.0f;
        float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0,
                -leftRight, leftRight,
                -bottomTop, bottomTop,
                near, far);
    }

    private void setShaderTransform() {

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        float[] viewProjMatrix = new float[16];
        Matrix.multiplyMM(viewProjMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0);

        float[] cameraRotation = mTotalRotation.asRotationMatrix();

        float[] shaderTransform = new float[16];
        Matrix.multiplyMM(shaderTransform, 0, viewProjMatrix, 0, cameraRotation, 0);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "shaderTransform");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);
    }

    @Override
    public void onDrawFrame() {
        // TODO this should take into account the # of milliseconds between calls
        if (mThisFrameRotation != null) {
            mRotationalMomentum = mThisFrameRotation;
            mThisFrameRotation = null;
        } else {
            mRotationalMomentum = mRotationalMomentum.pow(0.99);
        }
        mTotalRotation = mRotationalMomentum.multiply(mTotalRotation);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        setShaderTransform();
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
