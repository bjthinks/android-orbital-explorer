package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

class Texture {

    private final int id;
    private final int format, type, internalFormat;

    int getId() {
        return id;
    }

    Texture(int inFormat, int inType, int inInternalFormat) {
        format = inFormat;
        type = inType;
        internalFormat = inInternalFormat;

        int[] temp = new int[1];
        GLES30.glGenTextures(1, temp, 0);
        id = temp[0];

        // Bind the texture and set its initial size to 1x1
        bindToTexture2DAndResize(1, 1);

        // Fixed functionality filtering is too limited in its acceptable data types,
        // so we need to disable it for our textures (otherwise texture lookups fail).
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        // For all of our textures, we want no wrapping. This is irrelevant for texelFetch(),
        // so it's just future-proofing.
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        MyGL.checkGLES();
    }

    void bindToTexture2D() {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id);
    }

    void bindToTexture2DAndResize(int width, int height) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, internalFormat,
                width, height, 0, format, type, null);
    }

    void bindToTexture2DAndSetImage(int width, int height, float[] pixels) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, internalFormat,
                width, height, 0, format, type, MyGL.floatArrayToBuffer(pixels));
    }

    /* void delete() {
        int temp[] = new int[1];
        temp[0] = id;
        GLES30.glDeleteTextures(1, temp, 0);
    } */
}
