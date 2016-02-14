package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

public class Integrator extends RenderStage {

    private static final String TAG = "Integrator";

    AssetManager assetManager;
    AppPreferences appPreferences;
    boolean realOrbital = false;

    private int program;

    Orbital orbital, newOrbital;

    private final int RADIAL_TEXTURE_SIZE = 256;
    private Texture radialTexture;

    private final int AZIMUTHAL_TEXTURE_SIZE = 256;
    private Texture azimuthalTexture;

    private int quadratureDataSize;
    private Texture quadratureTexture;

    private Texture outputTexture;
    private Framebuffer framebuffer;
    private int width, height;

    public Texture getTexture() {
        return outputTexture;
    }

    Integrator(Context context) {
        appPreferences = new AppPreferences(context);
        assetManager = context.getAssets();
    }

    // Main thread
    public synchronized void orbitalChanged(Orbital newOrbital_) {
        newOrbital = newOrbital_;
    }

    public synchronized void realFlagChanged(boolean realOrbital_) {
        realOrbital = realOrbital_;
    }

    // Rendering thread
    private synchronized boolean checkForNewOrbital() {
        if (newOrbital != null) {
            orbital = newOrbital;
            newOrbital = null;
            return true;
        } else {
            return false;
        }
    }

    private void setupOrbitalTextures() {

        // Old textures? Trash them. (Deleting in the reverse order of creation is better
        // for the driver's digestion.)
        if (quadratureTexture != null)
            quadratureTexture.delete();
        if (azimuthalTexture != null)
            azimuthalTexture.delete();
        if (radialTexture != null)
            radialTexture.delete();

        // Create radial texture
        radialTexture = new Texture(GLES30.GL_RG, GLES30.GL_FLOAT, GLES30.GL_RG32F);
        float[] radialData
                = functionToBuffer2(orbital.getRadialFunction().getOscillatingPart(),
                0.0, orbital.getRadialFunction().getMaximumRadius(), RADIAL_TEXTURE_SIZE - 1);
        radialTexture.bindToTexture2DAndSetImage(RADIAL_TEXTURE_SIZE, 1, radialData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create azimuthal texture
        azimuthalTexture = new Texture(GLES30.GL_RG, GLES30.GL_FLOAT, GLES30.GL_RG32F);
        float[] azimuthalData = functionToBuffer2(orbital.getAzimuthalFunction(),
                0.0, Math.PI, AZIMUTHAL_TEXTURE_SIZE - 1);
        azimuthalTexture.bindToTexture2DAndSetImage(AZIMUTHAL_TEXTURE_SIZE, 1, azimuthalData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create quadrature texture
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);
        float[] quadratureData = QuadratureTable.get(assetManager, orbital.N, orbital.L);
        quadratureDataSize = quadratureData.length / (4 * orbital.getQuadratureOrder());
        quadratureTexture.bindToTexture2DAndSetImage(
                orbital.getQuadratureOrder(), quadratureDataSize, quadratureData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
    }

    public void newContext() {

        // Clear input textures, cuz whatever used to be there is gone now
        radialTexture = null;
        azimuthalTexture = null;
        quadratureTexture = null;

        // Create a texture to render to.
        // The following parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).
        outputTexture = new Texture(GLES30.GL_RGBA_INTEGER, GLES30.GL_SHORT, GLES30.GL_RGBA16I);
        outputTexture.bindToTexture2DAndResize(1, 1);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        framebuffer = new Framebuffer();
        framebuffer.bindAndSetTexture(outputTexture);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "integrator.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "integrator.frag", GLES30.GL_FRAGMENT_SHADER);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader.getId());
        GLES30.glAttachShader(program, fragmentShader.getId());
        GLES30.glLinkProgram(program);
        getGLError();
    }

    public void resize(int w, int h) {
        width = w;
        height = h;
        outputTexture.bindToTexture2DAndResize(width, height);
    }

    public void render(float[] shaderTransform) {

        if (checkForNewOrbital())
            setupOrbitalTextures();

        framebuffer.bindToAttachmentPoint();
        GLES30.glViewport(0, 0, width, height);

        final int zeroes[] = {0, 0, 0, 0};
        GLES30.glClearBufferiv(GLES30.GL_COLOR, 0, zeroes, 0);

        if (orbital != null) {
            GLES30.glUseProgram(program);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            radialTexture.bindToTexture2D();
            setUniformInt("radial", 0);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            azimuthalTexture.bindToTexture2D();
            setUniformInt("azimuthal", 1);

            GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
            quadratureTexture.bindToTexture2D();
            setUniformInt("quadrature", 2);

            setUniformInt("enableColor", appPreferences.getEnableColor() ? 1 : 0);
            setUniformInt("realOrbital", realOrbital ? 1 : 0);
            setUniformInt("numQuadraturePoints", orbital.getQuadratureOrder());

            RadialFunction radialFunction = orbital.getRadialFunction();

            // Multiply by 2 because the wave function is squared
            double exponentialConstant = 2.0 * radialFunction.getExponentialConstant();
            setUniformFloat("exponentialConstant", (float) exponentialConstant);

            // Multiply by 2 because the wave function is squared
            int radialPower = 2 * radialFunction.getPowerOfR();
            setUniformFloat("powerOfR", (float) radialPower);

            setUniformFloat("maximumRadius",
                    (float) orbital.getRadialFunction().getMaximumRadius());
            setUniformFloat("numRadialSubdivisions", (float) (RADIAL_TEXTURE_SIZE - 1));
            setUniformFloat("numAzimuthalSubdivisions", (float) (AZIMUTHAL_TEXTURE_SIZE - 1));
            setUniformFloat("numQuadratureSubdivisions", (float) (quadratureDataSize - 1));
            setUniformFloat("M", (float) orbital.M);

            // For testing
            setUniformFloat("zero", 0.0f);
            setUniformFloat("one", 1.0f);

            int mvpMatrixHandle = GLES30.glGetUniformLocation(program, "shaderTransform");
            GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, shaderTransform, 0);

            int inPositionHandle = GLES30.glGetAttribLocation(program, "inPosition");
            GLES30.glEnableVertexAttribArray(inPositionHandle);
            GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8,
                    screenRectangle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
            GLES30.glDisableVertexAttribArray(inPositionHandle);
        }

        getGLError();
    }

    void setUniformInt(String name, int value) {
        int handle = GLES30.glGetUniformLocation(program, name);
        GLES30.glUniform1i(handle, value);
    }

    void setUniformFloat(String name, float value) {
        int handle = GLES30.glGetUniformLocation(program, name);
        GLES30.glUniform1f(handle, value);
    }
}
