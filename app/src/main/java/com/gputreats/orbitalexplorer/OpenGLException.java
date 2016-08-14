package com.gputreats.orbitalexplorer;

class OpenGLException extends RuntimeException {

    OpenGLException(int code) {
        super(Integer.toString(code, 16));
    }

    OpenGLException(String err) {
        super(err);
    }
}
