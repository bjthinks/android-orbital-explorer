package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

class Program {

    private int id;
    int getId() {
        return id;
    }

    Program(AssetManager assets, String vertexFilename, String fragmentFilename) {
        Shader vertex = new Shader(assets, vertexFilename, GLES30.GL_VERTEX_SHADER);
        MyGL.checkGLES();
        Shader fragment = new Shader(assets, fragmentFilename, GLES30.GL_FRAGMENT_SHADER);
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

    void use() {
        GLES30.glUseProgram(id);
    }

    int getAttribLocation(String name) {
        return GLES30.glGetAttribLocation(id, name);
    }

    int getUniformLocation(String name) {
        return GLES30.glGetUniformLocation(id, name);
    }

    void setUniform1i(String name, int value) {
        GLES30.glUniform1i(getUniformLocation(name), value);
    }

    void setUniform1f(String name, float value) {
        GLES30.glUniform1f(getUniformLocation(name), value);
    }
}
