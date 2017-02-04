package com.gputreats.orbitalexplorer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

enum FloatBufferFactory {
    ;
    static FloatBuffer make(float[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer fb = byteBuffer.asFloatBuffer();
        fb.put(data);
        fb.position(0);
        return fb;
    }
}
