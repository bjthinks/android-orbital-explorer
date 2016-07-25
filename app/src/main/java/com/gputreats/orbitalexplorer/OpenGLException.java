package com.gputreats.orbitalexplorer;

public class OpenGLException extends RuntimeException {

    public OpenGLException(int code) {
        super(Integer.toString(code, 16));
    }

    public OpenGLException(String err) {
        super(err);
    }

}
