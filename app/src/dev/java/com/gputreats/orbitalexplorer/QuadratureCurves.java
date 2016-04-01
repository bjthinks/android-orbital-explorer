package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import com.gputreats.orbitalexplorer.*;
import com.gputreats.orbitalexplorer.Quadrature;
import com.gputreats.orbitalexplorer.RadialFunction;

import java.nio.FloatBuffer;

public class QuadratureCurves extends RenderStage {

    private AssetManager assetManager;
    private int width, height;
    private int program;

    public QuadratureCurves(Context context) {
        assetManager = context.getAssets();
        width = 1;
        height = 1;
    }

    public void newContext() {
        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "a", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "b", GLES30.GL_FRAGMENT_SHADER);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader.getId());
        GLES30.glAttachShader(program, fragmentShader.getId());
        GLES30.glLinkProgram(program);
        getGLError();
    }

    public void resize(int width_, int height_) {
        width = width_;
        height = height_;
    }

    private boolean firstTime = true;
    private int quadratureSize;
    private int quadratureOrder;
    private FloatBuffer quadratureBuffers[];

    public void render(RenderState.FrozenState frozenState) {
        double cameraDistance = frozenState.cameraDistance;

        if (firstTime || frozenState.orbitalChanged) {
            firstTime = false;
            Orbital orbital = frozenState.orbital;
            RadialFunction radialFunction = orbital.getRadialFunction();
            Quadrature quadrature = orbital.getQuadrature();
            quadratureSize = quadrature.getSize();
            float[] quadratureTable = QuadratureTable.get(assetManager, quadrature);
            float q[] = new float[2 * quadratureSize];
            quadratureOrder = quadratureTable.length / 4 / quadratureSize;
            double orbitalRadius = radialFunction.getMaximumRadius();
            quadratureBuffers = new FloatBuffer[quadratureOrder];
            for (int node = 0; node < quadratureOrder; ++ node) {
                for (int i = 0; i < quadratureSize; ++i) {
                    q[2 * i] = (float) (i * orbitalRadius / (quadratureSize - 1));
                    q[2 * i + 1] = quadratureTable[4 * quadratureOrder * i + 4 * node];
                }

                quadratureBuffers[node] = floatArrayToBuffer(q);
            }
        }

        if (quadratureBuffers != null) {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            GLES30.glViewport(0, 0, width, height);
            GLES30.glUseProgram(program);

            int scaleHandle = GLES30.glGetUniformLocation(program, "scale");
            float isotropicScale = 1.0f;
            float aspect = (float) Math.sqrt((double) width / (double) height);
            GLES30.glUniform2f(scaleHandle, isotropicScale / aspect / (float) cameraDistance,
                    isotropicScale * aspect / (float) cameraDistance);

            int positionHandle = GLES30.glGetAttribLocation(program, "position");
            GLES30.glEnableVertexAttribArray(positionHandle);

            for (int node = 0; node < quadratureOrder; ++node) {
                GLES30.glVertexAttribPointer(positionHandle, 2, GLES30.GL_FLOAT, false, 0,
                        quadratureBuffers[node]);
                GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, quadratureSize);
            }

            GLES30.glDisableVertexAttribArray(positionHandle);
        }

        getGLError();
    }
}
