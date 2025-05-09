package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;

import java.util.Arrays;

class Integrator extends RenderStage {

    private final AssetManager assets;

    private Program programColor, programMono;

    private Texture outputTextureColor, outputTextureMono;
    private Framebuffer framebufferColor, framebufferMono;
    private int width, height;
    private boolean outputTextureResized;

    Integrator(Context context) {
        assets = context.getAssets();
        outputTextureResized = true;
    }

    void onSurfaceCreated() {

        MyGL.checkGLES();

        // Create textures to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).

        // COLOR rendering
        outputTextureColor =
                new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        framebufferColor = new Framebuffer(outputTextureColor);

        // MONO rendering
        outputTextureMono = new Texture(GLES30.GL_RED_INTEGER, GLES30.GL_SHORT, GLES30.GL_R16I);
        framebufferMono = new Framebuffer(outputTextureMono);

        programColor = new Program(assets,
                "integrator_color.vert", "integrator_color.frag");
        programMono = new Program(assets,
                "integrator_mono.vert", "integrator_mono.frag");
    }

    void resize(int w, int h) {
        if (BuildConfig.DEBUG)
            Log.i("Integrator", "Changing size to " + w + 'x' + h);

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
    private Orbital orbital;
    Texture render(OrbitalData orbitalData, float[] newInverseTransform) {
        MyGL.checkGLES();

        boolean needToIntegrate = false;

        if (!Arrays.equals(inverseTransform, newInverseTransform)) {
            needToIntegrate = true;
            inverseTransform = Arrays.copyOf(newInverseTransform, newInverseTransform.length);
        }

        if (orbital == null || orbital.notEquals(orbitalData.getOrbital())) {
            needToIntegrate = true;
            orbital = orbitalData.getOrbital();
        }

        boolean color = orbitalData.getColor();

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
            final int[] zeroes = {0, 0, 0, 0};
            GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);

            orbitalData.setupForIntegration(program);

            int inverseTransformHandle = program.getUniformLocation("inverseTransform");
            GLES30.glUniformMatrix4fv(inverseTransformHandle, 1, false, inverseTransform, 0);

            int inPositionHandle = GLES30.glGetAttribLocation(program.getId(), "inPosition");
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
