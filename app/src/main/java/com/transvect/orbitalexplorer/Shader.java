package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shader {

    private static final String TAG = "Shader";

    private int id;

    public int getId() {
        return id;
    }

    public Shader(AssetManager assetManager, String filename, int shaderType) {
        String shaderSource = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    assetManager.open("shaders/" + filename)));
            String line;
            StringBuilder buf = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append('\n');
            }
            reader.close();
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
            throw new RuntimeException("Error compiling shader: " + result);
        }
    }
}
