package com.transvect.orbitalexplorer;

// Encapsulate an ancillary framebuffer for off-screen rendering

import android.opengl.GLES30;

public class Framebuffer {
    private int mFramebufferId;

    public Framebuffer() {
        // Generate framebuffer
        int temp[] = new int[1];
        GLES30.glGenFramebuffers(1, temp, 0);
        mFramebufferId = temp[0];
    }

    public Framebuffer(int id) {
        mFramebufferId = id;
    }

    public void bindToAttachmentPoint() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebufferId);
    }
}
