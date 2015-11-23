package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;

public class ColorModel extends RenderStage {
    private static final String TAG = "ColorModel";

    private FloatBuffer mVertexBuffer;
    private int mProgram;
    private Texture mTexture;
    private Framebuffer mFramebuffer;
    private int mFramebufferId;
    private int mWidth, mHeight;

    public Texture getTexture() {
        return mTexture;
    }

    public ColorModel() {
        float squareCoordinates[] = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f,
                 1.0f, -1.0f,
        };
        mVertexBuffer = floatArrayToBuffer(squareCoordinates);
    }

    public void newContext(AssetManager assetManager) {
        // The following three parameters have to match a row of Table 3.2 in the
        // OpenGL ES 3.0 specification, or we will get an OpenGL error. We also
        // need to choose a sized internal format which is color-renderable
        // according to Table 3.13 (in the absence of extensions).
        // Since this output will be enlarged, we also want texture-filterable.
        final int renderFormat = GLES30.GL_RGBA;
        final int renderType = GLES30.GL_UNSIGNED_INT_2_10_10_10_REV;
        final int renderInternalFormat = GLES30.GL_RGB10_A2;

        // Create a texture to render to
        mTexture = new Texture(renderFormat, renderType, renderInternalFormat);
        mTexture.bindToTexture2DAndResize(1, 1);

        // Set the filters for sampling the bound texture, when sampling at
        // a different resolution than native.
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        // Generate framebuffer
        mFramebuffer = new Framebuffer();
        int temp[] = new int[1];
        GLES30.glGenFramebuffers(1, temp, 0);
        mFramebufferId = temp[0];

        // Bind framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);

        // Attach the texture to the bound framebuffer
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, mTexture.getId(), 0);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
            Log.e(TAG, "Framebuffer not complete");

        // Un-bind framebuffer -- this returns drawing to the default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        getGLError();

        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "final.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "final.frag", GLES30.GL_FRAGMENT_SHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader.getId());
        GLES30.glAttachShader(mProgram, fragmentShader.getId());
        GLES30.glLinkProgram(mProgram);
        getGLError();
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mTexture.bindToTexture2DAndResize(mWidth, mHeight);
    }

    public void render(Texture texture) {
        GLES30.glViewport(0, 0, mWidth, mHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        texture.bindToTexture2D();
        int dataHandle = GLES30.glGetUniformLocation(mProgram, "data");
        GLES30.glUniform1i(dataHandle, 0);

        int inPositionHandle = GLES30.glGetAttribLocation(mProgram, "inPosition");
        GLES30.glEnableVertexAttribArray(inPositionHandle);
        GLES30.glVertexAttribPointer(inPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
        GLES30.glDisableVertexAttribArray(inPositionHandle);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        getGLError();
    }
}
