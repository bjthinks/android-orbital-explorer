package com.gputreats.orbitalexplorer;

class OpenGLException extends RuntimeException {

    private static final long serialVersionUID = 0;
    private static final int RADIX = 16;
    OpenGLException(int code) {
        super(Integer.toString(code, RADIX));
    }

    OpenGLException(String err) {
        super(err);
    }
}
