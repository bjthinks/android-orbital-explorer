package com.gputreats.orbitalexplorer;

import java.nio.FloatBuffer;

class RenderStage {

    final FloatBuffer screenRectangle;
    RenderStage() {
        float squareCoordinates[] = {
                -1, -1,
                -1,  1,
                 1,  1,
                 1, -1,
        };
        screenRectangle = MyGL.floatArrayToBuffer(squareCoordinates);
    }
}
