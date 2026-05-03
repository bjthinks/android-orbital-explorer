package com.gputreats.orbitalexplorer;

import java.io.Serial;

class OpenGLException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0;
    private static final int RADIX = 16;
    OpenGLException(int code) {
        super(Integer.toString(code, RADIX));
    }

    OpenGLException(String err) {
        super(err);
    }
}
