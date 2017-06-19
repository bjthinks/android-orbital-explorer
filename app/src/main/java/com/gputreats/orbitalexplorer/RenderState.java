package com.gputreats.orbitalexplorer;

import android.opengl.GLSurfaceView;
import android.os.Parcel;
import android.os.Parcelable;

class RenderState implements Parcelable {

    private OrbitalView orbitalView;

    Orbital orbital;

    RenderState() {
        orbital = new Orbital(1, 4, 2, 1, false, true);
    }

    // This happens BEFORE the render thread starts up

    synchronized void setOrbitalView(OrbitalView ov) {
        orbitalView = ov;
    }

    // Parcelable stuff

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public synchronized void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(orbital, flags);
    }

    public static final Parcelable.Creator<RenderState> CREATOR
            = new Parcelable.Creator<RenderState>() {
        @Override
        public RenderState createFromParcel(Parcel source) {
            RenderState result = new RenderState();
            result.orbital = source.readParcelable(Orbital.class.getClassLoader());
            return result;
        }
        @Override
        public RenderState[] newArray(int size) {
            return new RenderState[size];
        }
    };

    // Main thread interface
    synchronized Orbital getOrbital() {
        return orbital;
    }

    synchronized void setOrbital(int qZ, int qN, int qL, int qM, boolean real, boolean color) {
        Orbital newOrbital = new Orbital(qZ, qN, qL, qM, real, color);
        if (newOrbital.notEquals(orbital)) {
            orbital = new Orbital(qZ, qN, qL, qM, real, color);
            if (orbitalView != null) {
                if (color)
                    orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                else {
                    orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    orbitalView.requestRender();
                }
            }
        }
    }

    // Render thread getter

    synchronized Orbital freeze() {
        return orbital;
    }
}
