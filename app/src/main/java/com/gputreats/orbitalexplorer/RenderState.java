package com.gputreats.orbitalexplorer;

import android.opengl.GLSurfaceView;
import android.os.Parcel;
import android.os.Parcelable;

public class RenderState implements Parcelable {

    private OrbitalView orbitalView;

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

    // This happens BEFORE the render thread starts up
    public void setOrbitalView(OrbitalView ov) {
        orbitalView = ov;
    }

    // This happens AFTER the render thread starts up
    public void finalSetup() {
        if (!orbital.color) {
            orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            orbitalView.requestRender();
        }
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
        if (orbitalView != null && !orbital.color)
            orbitalView.requestRender();
    }

    public synchronized void toggleColor() {
        boolean color = !orbital.color;
        orbital = new Orbital(orbital.Z, orbital.N, orbital.L, orbital.M,
                orbital.real, color);
        orbitalChanged = true;
        if (color)
            orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        else {
            orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            orbitalView.requestRender();
        }
    }

    public synchronized boolean cameraStopFling() {
        return camera.stopFling();
    }

    public synchronized void cameraDrag(double dx, double dy) {
        camera.drag(dx, dy);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    public synchronized void cameraTwist(double angle) {
        camera.twist(angle);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    public synchronized void cameraZoom(double factor) {
        camera.zoom(factor);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
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
        // if (!stillFlinging && !orbital.color) orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
