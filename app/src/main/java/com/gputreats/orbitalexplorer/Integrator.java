package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.util.Arrays;

class Integrator extends RenderStage {

    private AssetManager assets;

    private Program programColor, programMono;

    private Texture outputTextureColor, outputTextureMono;
    private Framebuffer framebufferColor, framebufferMono;
    private int width, height;
    private boolean outputTextureResized;

    Integrator(Context context) {
        assets = context.getAssets();
        outputTextureResized = true;
    }

    void onSurfaceCreated() throws OpenGLException {

        MyGL.checkGLES();

        // Create textures to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).

        // COLOR rendering
        outputTextureColor = new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        framebufferColor = new Framebuffer(outputTextureColor);

        // MONO rendering
        outputTextureMono = new Texture(GLES30.GL_RED_INTEGER, GLES30.GL_SHORT, GLES30.GL_R16I);
        framebufferMono = new Framebuffer(outputTextureMono);

        programColor = new Program(assets, "2", "1");
        programMono = new Program(assets, "4", "3");
    }

    void resize(int w, int h) throws OpenGLException {
        width = w;
        height = h;
        outputTextureResized = true;

        MyGL.checkGLES();
        outputTextureColor.bindToTexture2DAndResize(width, height);
        MyGL.checkGLES();
        outputTextureMono.bindToTexture2DAndResize(width, height);
        MyGL.checkGLES();
    }

    private float[] inverseTransform = new float[16];
    Texture render(OrbitalTextures orbitalTextures,
                   float[] newInverseTransform, boolean needToIntegrate)
            throws OpenGLException {

        MyGL.checkGLES();

        if (!Arrays.equals(inverseTransform, newInverseTransform)) {
            needToIntegrate = true;
            inverseTransform = Arrays.copyOf(newInverseTransform, newInverseTransform.length);
        }

        boolean color = orbitalTextures.getColor();

        if (needToIntegrate || outputTextureResized) {
            outputTextureResized = false; // Also needed for e.g. orientation changes

            Program program = color ? programColor : programMono;
            program.use();

            boolean savedDepthTest = GLES30.glIsEnabled(GLES30.GL_DEPTH_TEST);
            boolean savedScissorTest = GLES30.glIsEnabled(GLES30.GL_SCISSOR_TEST);
            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
            GLES30.glDisable(GLES30.GL_SCISSOR_TEST);

            Framebuffer framebuffer = color ? framebufferColor : framebufferMono;
            framebuffer.bindToAttachmentPoint();
            GLES30.glViewport(0, 0, width, height);

            // TODO does ditching this improve anything?
            final int zeroes[] = {0, 0, 0, 0};
            GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);

            orbitalTextures.setupForIntegration(program);

            int mvpMatrixHandle = program.getUniformLocation("inverseTransform");
            GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, inverseTransform, 0);

            int inPositionHandle;
            inPositionHandle = GLES30.glGetAttribLocation(program.getId(), "inPosition");
            GLES30.glEnableVertexAttribArray(inPositionHandle);
            GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                    screenRectangle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
            GLES30.glDisableVertexAttribArray(inPositionHandle);

            framebuffer.unbindFromAttachmentPoint();

            if (savedDepthTest)
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            if (savedScissorTest)
                GLES30.glEnable(GLES30.GL_SCISSOR_TEST);
        }

        MyGL.checkGLES();

        return color ? outputTextureColor : outputTextureMono;
    }
}
