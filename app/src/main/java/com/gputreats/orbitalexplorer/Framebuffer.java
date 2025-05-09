package com.gputreats.orbitalexplorer;

// Encapsulate an ancillary framebuffer for off-screen rendering

import android.opengl.GLES30;

class Framebuffer {

    private final int framebufferId;
    private final int[] oldFramebuffer = new int[1];

    Framebuffer(Texture texture) {
        // Generate framebuffer
        int[] temp = new int[1];
        GLES30.glGenFramebuffers(1, temp, 0);
        framebufferId = temp[0];

        bindToAttachmentPoint();
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, texture.getId(), 0);

        // Check if framebuffer is complete
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
            throw new OpenGLException("Framebuffer not complete");

        unbindFromAttachmentPoint();

        MyGL.checkGLES();
    }

    void bindToAttachmentPoint() {
        GLES30.glGetIntegerv(GLES30.GL_DRAW_FRAMEBUFFER_BINDING, oldFramebuffer, 0);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, framebufferId);
    }

    void unbindFromAttachmentPoint() {
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, oldFramebuffer[0]);
    }
}
