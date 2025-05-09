package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedInputStream;
import java.io.IOException;

class Shader {

    private final int id;
    int getId() {
        return id;
    }

    Shader(AssetManager assets, String filename, int shaderType) {
        String shaderSource;
        try {
            BufferedInputStream instream = new BufferedInputStream(
                    assets.open("shaders/" + filename));
            int b = instream.read();
            StringBuilder buf = new StringBuilder();
            while (b != -1) {
                buf.append((char) b);
                b = instream.read();
            }
            instream.close();
            shaderSource = buf.toString();
        } catch (IOException ignored) {
            throw new RuntimeException("Error reading shader: " + filename);
        }
        id = GLES30.glCreateShader(shaderType);
        GLES30.glShaderSource(id, shaderSource);
        GLES30.glCompileShader(id);
        int[] status = new int[1];
        GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES30.GL_TRUE) {
            String result = GLES30.glGetShaderInfoLog(id);
            String type = "";
            if (shaderType == GLES30.GL_VERTEX_SHADER)
                type = "vertex";
            else if (shaderType == GLES30.GL_FRAGMENT_SHADER)
                type = "fragment";
            throw new OpenGLException("Error compiling " + type + " shader: " + result);
        }
    }
}
