package com.gputreats.orbitalexplorer;

import android.os.Parcel;
import android.os.Parcelable;

public class RenderState implements Parcelable {

    private Camera camera;
    private boolean cameraChanged;
    private Orbital orbital;
    private boolean orbitalChanged;

    public RenderState() {
        camera = new Camera();
        cameraChanged = true;
        orbital = new Orbital(1, 4, 2, 1, false, true);
        orbitalChanged = true;
    }

    // Parcelable stuff
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public synchronized void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(camera, flags);
        out.writeInt(orbital.Z);
        out.writeInt(orbital.N);
        out.writeInt(orbital.L);
        out.writeInt(orbital.M);
        out.writeInt(orbital.real ? 1 : 0);
        out.writeInt(orbital.color ? 1 : 0);
    }

    public static final Parcelable.Creator<RenderState> CREATOR
            = new Parcelable.Creator<RenderState>() {
        @Override
        public RenderState createFromParcel(Parcel in) {
            RenderState result = new RenderState();
            result.camera = in.readParcelable(Camera.class.getClassLoader());
            result.cameraChanged = true;
            int Z = in.readInt();
            int N = in.readInt();
            int L = in.readInt();
            int M = in.readInt();
            boolean real = (in.readInt() != 0);
            boolean color = (in.readInt() != 0);
            result.orbital = new Orbital(Z, N, L, M, real, color);
            result.orbitalChanged = true;
            return result;
        }
        @Override
        public RenderState[] newArray(int size) {
            return new RenderState[size];
        }
    };

    // Main thread interface
    public synchronized Orbital getOrbital() {
        return orbital;
    }

    public synchronized void setOrbital(int Z, int N, int L, int M, boolean real) {
        orbital = new Orbital(Z, N, L, M, real, orbital.color);
        orbitalChanged = true;
    }

    public synchronized void toggleColor() {
        orbital = new Orbital(orbital.Z, orbital.N, orbital.L, orbital.M,
                orbital.real, !orbital.color);
        orbitalChanged = true;
    }

    public synchronized boolean cameraStopFling() {
        return camera.stopFling();
    }

    public synchronized void cameraDrag(double dx, double dy) {
        camera.drag(dx, dy);
        cameraChanged = true;
    }

    public synchronized void cameraTwist(double angle) {
        camera.twist(angle);
        cameraChanged = true;
    }

    public synchronized void cameraZoom(double factor) {
        camera.zoom(factor);
        cameraChanged = true;
    }

    public synchronized void cameraFling(double vx, double vy) {
        camera.fling(vx, vy);
        cameraChanged = true;
    }

    // Render thread getter
    public synchronized FrozenState freeze(double aspectRatio) {
        FrozenState fs = new FrozenState();

        boolean stillFlinging = camera.continueFling();
        fs.inverseTransform = camera.computeInverseShaderTransform(aspectRatio);
        fs.cameraDistance = camera.getCameraDistance();
        fs.orbital = orbital;
        fs.orbitalChanged = orbitalChanged;
        fs.needToIntegrate = orbitalChanged || cameraChanged || stillFlinging;
        // if (fs.needToIntegrate || orbital.color) needToDrawScreen = true;

        orbitalChanged = false;
        cameraChanged = false;

        return fs;
    }

    static public class FrozenState {
        public float[] inverseTransform;
        public double cameraDistance;
        public Orbital orbital;
        public boolean orbitalChanged;
        public boolean needToIntegrate;
    }
}
