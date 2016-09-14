package com.gputreats.orbitalexplorer;

import android.os.Handler;

class FrozenState {
    float[] inverseTransform;
    Orbital orbital;
    boolean needToIntegrate;
    boolean screenGrabRequested;
    Handler screenGrabHandler;
}
