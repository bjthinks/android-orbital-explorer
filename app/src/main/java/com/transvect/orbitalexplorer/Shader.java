package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Encapsulate an OpenGL vertex or fragment shader.
 */

public class Shader {
    private static final String TAG = "Shader";

    private int id;

    public int getId() {
        return id;
    }

    Shader(AssetManager assetManager, String filename, int shaderType) {
        BufferedReader reader = null;
        String shaderSource = "";
        try {
            reader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String line = reader.readLine();
            while (line != null) {
                shaderSource += line + "\n";
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        id = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(id, shaderSource);
        GLES20.glCompileShader(id);
        String result = GLES20.glGetShaderInfoLog(id);
        if (!result.equals(""))
            Log.e(TAG, result);
    }
}
