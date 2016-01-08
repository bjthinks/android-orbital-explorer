package com.transvect.orbitalexplorer;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    @Override public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        int vertex = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(vertex, "#version 300 es\n" +
                "void main() {}\n");
        GLES30.glCompileShader(vertex);
        int fragment = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(fragment, "#version 300 es\n" +
                "precision highp float;\n" +
                "out vec2 color;\n" +
                "void main() {\n" +
                "    vec2 total = vec2(0);\n" +
                "    for (int i = 0; i < 1; ++i) {\n" +
                "        float phi = atan(0.0, 1.0);\n" +
                "        total += vec2(1.0, 0.0);\n" +
                "    }\n" +
                "    color = total * 2.0;\n" +
                "}\n");
        GLES30.glCompileShader(fragment);
        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertex);
        GLES30.glAttachShader(program, fragment);
        try {
            GLES30.glLinkProgram(program);
        } catch (Exception e) {
            Log.e("Renderer", e.getLocalizedMessage());
        }
    }
    @Override public void onSurfaceChanged(GL10 xx, int width, int height) {}
    @Override public void onDrawFrame(GL10 xx) {}
}
