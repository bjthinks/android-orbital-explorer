package com.transvect.orbitalexplorer;

import android.opengl.GLES30;

import java.nio.Buffer;

public class Texture {

    private final int mId;
    private final int mFormat, mType, mInternalFormat;

    public int getId() {
        return mId;
    }

    Texture(int format, int type, int internalFormat) {
        mFormat = format;
        mType = type;
        mInternalFormat = internalFormat;

        int temp[] = new int[1];
        GLES30.glGenTextures(1, temp, 0);
        mId = temp[0];
    }

    public void bindToTexture2D() {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mId);
    }

    public void bindToTexture2DAndResize(int width, int height) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mInternalFormat,
                width, height, 0, mFormat, mType, null);
    }

    public void bindToTexture2DAndSetImage(int width, int height, Buffer pixels) {
        bindToTexture2D();
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mInternalFormat,
                width, height, 0, mFormat, mType, pixels);
    }
}
