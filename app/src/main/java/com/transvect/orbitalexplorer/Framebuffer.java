package com.transvect.orbitalexplorer;

// Encapsulate an ancillary framebuffer for off-screen rendering

import android.opengl.GLES30;
import android.util.Log;

public class Framebuffer {
    private static final String TAG = "Framebuffer";

    private int mFramebufferId;

    public Framebuffer() {
        // Generate framebuffer
        int temp[] = new int[1];
        GLES30.glGenFramebuffers(1, temp, 0);
        mFramebufferId = temp[0];
    }

    public void bindToAttachmentPoint() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);
    }

    public void bindAndSetTexture(Texture texture) {
        bindToAttachmentPoint();
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, texture.getId(), 0);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
            Log.e(TAG, "Framebuffer not complete");
    }
}
