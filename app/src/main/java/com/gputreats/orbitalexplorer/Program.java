package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

public class Program {

    private int id;
    public int getId() {
        return id;
    }

    public Program(AssetManager assetManager, String vertexFilename, String fragmentFilename) {
        Shader vertex = new Shader(assetManager, vertexFilename, GLES30.GL_VERTEX_SHADER);
        MyGL.checkGLES();
        Shader fragment = new Shader(assetManager, fragmentFilename, GLES30.GL_FRAGMENT_SHADER);
        MyGL.checkGLES();
        id = GLES30.glCreateProgram();
        MyGL.checkGLES();
        GLES30.glAttachShader(id, vertex.getId());
        MyGL.checkGLES();
        GLES30.glAttachShader(id, fragment.getId());
        MyGL.checkGLES();
        GLES30.glLinkProgram(id);
        MyGL.checkGLES();
    }

    void setUniform(String name, int value) {
        int location = GLES30.glGetUniformLocation(id, name);
        GLES30.glUniform1i(location, value);
    }
}
