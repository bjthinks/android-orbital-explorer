package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.util.Arrays;

public class Integrator extends RenderStage {

    AssetManager assetManager;

    private int programColor, programMono, currentProgram;

    private final int RADIAL_TEXTURE_SIZE = 256;
    private Texture radialTexture;

    private final int AZIMUTHAL_TEXTURE_SIZE = 256;
    private Texture azimuthalTexture;

    private int quadratureDataSize;
    private Texture quadratureTexture;

    private Texture outputTextureColor, outputTextureMono;
    private Framebuffer framebufferColor, framebufferMono;
    private int width, height;
    private boolean newRenderer, needToRender;

    Integrator(Context context) {
        assetManager = context.getAssets();
        newRenderer = true;
        needToRender = true;
    }

    public void newContext() {

        // Create input textures for storing the radial, azimuthal, and quadrature data.
        radialTexture     = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        azimuthalTexture  = new Texture(GLES30.GL_RG,   GLES30.GL_FLOAT, GLES30.GL_RG32F);
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);

        // Floating point textures are not filterable
        radialTexture.bindToTexture2D();
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        azimuthalTexture.bindToTexture2D();
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        quadratureTexture.bindToTexture2D();
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create textures to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).
        outputTextureColor = new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        outputTextureColor.bindToTexture2DAndResize(1, 1);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        outputTextureMono = new Texture(GLES30.GL_RED_INTEGER, GLES30.GL_SHORT, GLES30.GL_R16I);
        outputTextureMono.bindToTexture2DAndResize(1, 1);

        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        // Create framebuffers to render to
        framebufferColor = new Framebuffer();
        framebufferColor.bindAndSetTexture(outputTextureColor);

        framebufferMono = new Framebuffer();
        framebufferMono.bindAndSetTexture(outputTextureMono);

        getGLError();

        // Compile & link GLSL programs
        Shader vertexShaderColor
                = new Shader(assetManager, "integrator_color.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShaderColor
                = new Shader(assetManager, "integrator_color.frag", GLES30.GL_FRAGMENT_SHADER);
        programColor = GLES30.glCreateProgram();
        GLES30.glAttachShader(programColor, vertexShaderColor.getId());
        GLES30.glAttachShader(programColor, fragmentShaderColor.getId());
        GLES30.glLinkProgram(programColor);
        getGLError();

        Shader vertexShaderMono
                = new Shader(assetManager, "integrator_mono.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShaderMono
                = new Shader(assetManager, "integrator_mono.frag", GLES30.GL_FRAGMENT_SHADER);
        programMono = GLES30.glCreateProgram();
        GLES30.glAttachShader(programMono, vertexShaderMono.getId());
        GLES30.glAttachShader(programMono, fragmentShaderMono.getId());
        GLES30.glLinkProgram(programMono);
        getGLError();
    }

    public void resize(int w, int h) {
        width = w;
        height = h;
        outputTextureColor.bindToTexture2DAndResize(width, height);
        outputTextureMono.bindToTexture2DAndResize(width, height);
        needToRender = true;
    }

    private float[] oldTransform;
    public Texture render(RenderState.FrozenState frozenState) {

        float[] inverseTransform = frozenState.inverseTransform;
        Orbital orbital = frozenState.orbital;
        boolean orbitalChanged = frozenState.orbitalChanged;
        boolean color = frozenState.orbital.color;

        if (color)
            currentProgram = programColor;
        else
            currentProgram = programMono;

        if (orbitalChanged || newRenderer) {
            newRenderer = false;

            // Load new radial texture
            float[] radialData
                    = functionToBuffer2(orbital.getRadialFunction().getOscillatingPart(),
                    0.0, orbital.getRadialFunction().getMaximumRadius(), RADIAL_TEXTURE_SIZE - 1);
            radialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, radialData);

            // Load new azimuthal texture
            float[] azimuthalData = functionToBuffer2(orbital.getAzimuthalFunction(),
                    0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);
            azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

            // Load new quadrature texture
            float[] quadratureData = QuadratureTable.get(assetManager, orbital);
            quadratureDataSize = quadratureData.length
                    / (4 * orbital.getQuadrature().getOrder());
            quadratureTexture.bindToTexture2DAndSetImage(
                    orbital.getQuadrature().getOrder(),
                    quadratureDataSize, quadratureData);
        }

        if (oldTransform == null || !Arrays.equals(oldTransform, inverseTransform)) {
            oldTransform = inverseTransform;
            needToRender = true;
        }

        if (orbitalChanged || needToRender) {
            needToRender = false;

            if (color)
                framebufferColor.bindToAttachmentPoint();
            else
                framebufferMono.bindToAttachmentPoint();
            GLES30.glViewport(0, 0, width, height);

            final int zeroes[] = {0, 0, 0, 0};
            GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);

            GLES30.glUseProgram(currentProgram);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            radialTexture.bindToTexture2D();
            setUniformInt("radial", 0);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            azimuthalTexture.bindToTexture2D();
            setUniformInt("azimuthal", 1);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
            quadratureTexture.bindToTexture2D();
            setUniformInt("quadrature", 2);

            setUniformInt("enableColor", 1);
            setUniformInt("realOrbital", orbital.real ? 1 : 0);
            setUniformInt("numQuadraturePoints", orbital.getQuadrature().getOrder());

            RadialFunction radialFunction = orbital.getRadialFunction();

            // Multiply by 2 because the wave function is squared
            double exponentialConstant = 2.0 * radialFunction.getExponentialConstant();
            setUniformFloat("exponentialConstant", (float) exponentialConstant);

            // Multiply by 2 because the wave function is squared
            int radialPower = 2 * radialFunction.getPowerOfR();
            setUniformFloat("powerOfR", (float) radialPower);

            float maxR = (float) orbital.getRadialFunction().getMaximumRadius();
            setUniformFloat("maximumRadius", maxR);
            setUniformFloat("brightness", maxR * maxR / 5.0f);
            setUniformFloat("numRadialSubdivisions", (float) (RADIAL_TEXTURE_SIZE - 1));
            setUniformFloat("numAzimuthalSubdivisions", (float) (AZIMUTHAL_TEXTURE_SIZE - 1));
            setUniformFloat("numQuadratureSubdivisions", (float) (quadratureDataSize - 1));
            setUniformFloat("M", (float) orbital.M);

            int mvpMatrixHandle = getUniformHandle("inverseTransform");
            GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, inverseTransform, 0);

            int inPositionHandle;
            inPositionHandle = GLES30.glGetAttribLocation(currentProgram, "inPosition");
            GLES30.glEnableVertexAttribArray(inPositionHandle);
            GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                    screenRectangle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
            GLES30.glDisableVertexAttribArray(inPositionHandle);
        }

        getGLError();

        if (color)
            return outputTextureColor;
        else
            return outputTextureMono;
    }

    int getUniformHandle(String name) {
        return GLES30.glGetUniformLocation(currentProgram, name);
    }

    void setUniformInt(String name, int value) {
        GLES30.glUniform1i(getUniformHandle(name), value);
    }

    void setUniformFloat(String name, float value) {
        GLES30.glUniform1f(getUniformHandle(name), value);
    }
}
