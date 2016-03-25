package com.gputreats.orbitalexplorer;

import android.opengl.GLES30;

public class Texture {

    private final int id;
    private final int format, type, internalFormat;

    public int getId() {
        return id;
    }

    public Texture(int format_, int type_, int internalFormat_) {
        format = format_;
        type = type_;
        internalFormat = internalFormat_;

        int temp[] = new int[1];
        GLES30.glGenTextures(1, temp, 0);
        id = temp[0];
    }

    public void bindToTexture2D() {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id);
    }

    public void bindToTexture2DAndResize(int width, int height) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, internalFormat,
                width, height, 0, format, type, null);
    }

    public void bindToTexture2DAndSetImage(int width, int height, float[] pixels) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, internalFormat,
                width, height, 0, format, type, RenderStage.floatArrayToBuffer(pixels));
    }

    /* public void delete() {
        int temp[] = new int[1];
        temp[0] = id;
        GLES30.glDeleteTextures(1, temp, 0);
    } */
}
