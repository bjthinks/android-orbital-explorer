package com.gputreats.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Shader {

    private int id;
    public int getId() {
        return id;
    }

    public Shader(AssetManager assetManager, String filename, int shaderType) throws OpenGLException {
        String shaderSource;
        try {
            BufferedInputStream instream = new BufferedInputStream(
                    assetManager.open("a/" + filename));
            int b = instream.read();
            int c = filename.charAt(0);
            Spew spew = new Spew(c, c);
            StringBuilder buf = new StringBuilder();
            while (b != -1) {
                buf.append((char) (b ^ (spew.get() & 255)));
                b = instream.read();
            }
            instream.close();
            shaderSource = buf.toString();
        } catch (IOException e) {
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
                type = "vertex ";
            else if (shaderType == GLES30.GL_FRAGMENT_SHADER)
                type = "fragment ";
            throw new OpenGLException("Error compiling " + type + "shader:\n" + result);
        }
    }
}
