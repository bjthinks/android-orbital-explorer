package com.transvect.orbitalexplorer;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;
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
        id = GLES30.glCreateShader(shaderType);
        GLES30.glShaderSource(id, shaderSource);
        GLES30.glCompileShader(id);
        String result = GLES30.glGetShaderInfoLog(id);
        if (!result.equals(""))
            Log.e(TAG, result);
    }
}
