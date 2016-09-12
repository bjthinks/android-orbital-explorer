package com.gputreats.orbitalexplorer;

import java.nio.FloatBuffer;

class RenderStage {

    final FloatBuffer screenRectangle;
    RenderStage() {
        float[] squareCoordinates = {
                -1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f,
                 1.0f, -1.0f,
        };
        screenRectangle = MyGL.floatArrayToBuffer(squareCoordinates);
    }
}
