package com.transvect.orbitalexplorer;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

public class QuadratureCurves extends RenderStage {

    private AssetManager assetManager;
    private int width, height;
    private int program;

    public QuadratureCurves(Context context) {
        assetManager = context.getAssets();
        width = 1;
        height = 1;
    }

    public void newContext() {
        // Compile & link GLSL program
        Shader vertexShader = new Shader(assetManager, "quadraturecurves.vert", GLES30.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(assetManager, "quadraturecurves.frag", GLES30.GL_FRAGMENT_SHADER);
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader.getId());
        GLES30.glAttachShader(program, fragmentShader.getId());
        GLES30.glLinkProgram(program);
        getGLError();
    }

    public void resize(int width_, int height_) {
        width = width_;
        height = height_;
    }

    public void render() {
    }
}
