package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

public class Integrator extends RenderStage {

    AssetManager assetManager;

    private Program programColor, programMono;

    OrbitalTextures orbitalTextures;

    private Texture outputTextureColor, outputTextureMono;
    private Framebuffer framebufferColor, framebufferMono;
    private int width, height;
    private boolean outputTextureResized;

    Integrator(Context context) {
        assetManager = context.getAssets();
        outputTextureResized = true;
    }

    public void onSurfaceCreated() throws OpenGLException {

        MyGL.checkGLES();

        orbitalTextures = new OrbitalTextures(assetManager);

        // Create textures to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).

        // COLOR rendering

        outputTextureColor = new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        MyGL.checkGLES();
        outputTextureColor.bindToTexture2DAndResize(1, 1);
        MyGL.checkGLES();

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        MyGL.checkGLES();
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        MyGL.checkGLES();

        framebufferColor = new Framebuffer();
        MyGL.checkGLES();
        framebufferColor.bindAndSetTexture(outputTextureColor);
        MyGL.checkGLES();

        // MONO rendering

        outputTextureMono = new Texture(GLES30.GL_RED_INTEGER, GLES30.GL_SHORT, GLES30.GL_R16I);
        MyGL.checkGLES();
        outputTextureMono.bindToTexture2DAndResize(1, 1);
        MyGL.checkGLES();

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        MyGL.checkGLES();
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        MyGL.checkGLES();

        framebufferMono = new Framebuffer();
        MyGL.checkGLES();
        framebufferMono.bindAndSetTexture(outputTextureMono);
        MyGL.checkGLES();

        programColor = new Program(assetManager, "2", "1");
        programMono = new Program(assetManager, "4", "3");
    }

    public void resize(int w, int h) throws OpenGLException {
        width = w;
        height = h;
        outputTextureResized = true;

        MyGL.checkGLES();
        outputTextureColor.bindToTexture2DAndResize(width, height);
        MyGL.checkGLES();
        outputTextureMono.bindToTexture2DAndResize(width, height);
        MyGL.checkGLES();
    }

    public Texture render(RenderState.FrozenState frozenState) throws OpenGLException {

        MyGL.checkGLES();

        float[] inverseTransform = frozenState.inverseTransform;
        Orbital orbital = frozenState.orbital;
        boolean needToIntegrate = frozenState.needToIntegrate;

        Program currentProgram = orbital.color ? programColor : programMono;

        orbitalTextures.loadOrbital(orbital);

        if (needToIntegrate || outputTextureResized) {
            outputTextureResized = false; // Also needed for e.g. orientation changes

            if (orbital.color)
                framebufferColor.bindToAttachmentPoint();
            else
                framebufferMono.bindToAttachmentPoint();
            GLES30.glViewport(0, 0, width, height);

            final int zeroes[] = {0, 0, 0, 0};
            GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);

            GLES30.glUseProgram(currentProgram.getId());

            orbitalTextures.bindForRendering(currentProgram);

            int mvpMatrixHandle = currentProgram.getUniformLocation("inverseTransform");
            GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, inverseTransform, 0);

            int inPositionHandle;
            inPositionHandle = GLES30.glGetAttribLocation(currentProgram.getId(), "inPosition");
            GLES30.glEnableVertexAttribArray(inPositionHandle);
            GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                    screenRectangle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
            GLES30.glDisableVertexAttribArray(inPositionHandle);
        }

        MyGL.checkGLES();

        return orbital.color ? outputTextureColor : outputTextureMono;
    }
}
