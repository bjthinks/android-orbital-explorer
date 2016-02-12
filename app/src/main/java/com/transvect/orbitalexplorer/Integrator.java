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

    private int radialDataSize;
    private Texture radialTexture;

    private int azimuthalDataSize;
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
        float[] radialData = orbital.getRadialData();
        radialDataSize = radialData.length / 2;
        radialTexture.bindToTexture2DAndSetImage(radialDataSize, 1, radialData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create azimuthal texture
        azimuthalTexture = new Texture(GLES30.GL_RG, GLES30.GL_FLOAT, GLES30.GL_RG32F);
        float[] azimuthalData = orbital.getAzimuthalData();
        azimuthalDataSize = azimuthalData.length / 2;
        azimuthalTexture.bindToTexture2DAndSetImage(azimuthalDataSize, 1, azimuthalData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);

        // Create quadrature texture
        quadratureTexture = new Texture(GLES30.GL_RGBA, GLES30.GL_FLOAT, GLES30.GL_RGBA32F);
        float[] quadratureData = orbital.getQuadratureData(assetManager);
        quadratureDataSize = quadratureData.length / (4 * orbital.getNumQuadraturePoints());
        quadratureTexture.bindToTexture2DAndSetImage(
                orbital.getNumQuadraturePoints(), quadratureDataSize, quadratureData);

        // Floating point textures are not filterable
        setTexture2DMinMagFilters(GLES30.GL_NEAREST, GLES30.GL_NEAREST);
    }

    public void newContext(AssetManager assetManager_) {

        assetManager = assetManager_;

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
            setUniformInt("numQuadraturePoints", orbital.getNumQuadraturePoints());

            setUniformFloat("exponentialConstant", (float) (2.0 * orbital.getRadialExponent()));
            setUniformFloat("maximumRadius", (float) orbital.getMaximumRadius());
            setUniformFloat("numRadialSubdivisions", (float) (radialDataSize - 1));
            setUniformFloat("numAzimuthalSubdivisions", (float) (azimuthalDataSize - 1));
            setUniformFloat("numQuadratureSubdivisions", (float) (quadratureDataSize - 1));
            setUniformFloat("M", (float) orbital.getM());
            // Multiply by 2 because the wave function is squared
            setUniformFloat("powerOfR", (float) (2 * orbital.getRadialPower()));

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
