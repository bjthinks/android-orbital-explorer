package com.gputreats.orbitalexplorer;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

class RenderState implements Parcelable {

    private Handler renderExceptionHandler;
    private OrbitalView orbitalView;

    private Camera camera;
    private boolean cameraChanged;
    private Orbital orbital;
    private boolean orbitalChanged;
    private boolean screenGrabRequested;
    private Handler screenGrabHandler;

    RenderState() {
        camera = new Camera();
        cameraChanged = true;
        orbital = new Orbital(1, 4, 2, 1, false, true);
        orbitalChanged = true;
        screenGrabRequested = false;
    }

    // These happen BEFORE the render thread starts up

    void setRenderExceptionHandler(Handler h) {
        renderExceptionHandler = h;
    }

    void setOrbitalView(OrbitalView ov) {
        orbitalView = ov;
    }

    // This happens AFTER the render thread starts up

    void postRenderThreadSetup() {
        if (!orbital.color) {
            orbitalView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            orbitalView.requestRender();
        }
    }

    // Parcelable stuff

    @SuppressWarnings("MethodReturnAlwaysConstant")
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

    @SuppressWarnings({"AnonymousInnerClassWithTooManyMethods", "AnonymousInnerClass"})
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
            boolean real = in.readInt() != 0;
            boolean color = in.readInt() != 0;
            result.orbital = new Orbital(Z, N, L, M, real, color);
            result.orbitalChanged = true;
            result.screenGrabRequested = false;
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

    synchronized void setOrbital(int Z, int N, int L, int M, boolean real, boolean color) {
        Orbital newOrbital = new Orbital(Z, N, L, M, real, color);
        if (newOrbital.notEquals(orbital)) {
            Analytics.reportEvent("change", "("
                    + Integer.toString(N) + ","
                    + Integer.toString(L) + ","
                    + Integer.toString(M) + ","
                    + Integer.toString(real ? 1 : 0) + ","
                    + Integer.toString(color ? 1 : 0) + ")");
            orbital = new Orbital(Z, N, L, M, real, color);
            orbitalChanged = true;
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

    synchronized boolean cameraStopFling() {
        return camera.stopFling();
    }

    synchronized void cameraDrag(double dx, double dy) {
        camera.drag(dx, dy);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    synchronized void cameraTwist(double angle) {
        camera.twist(angle);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    synchronized void cameraZoom(double factor) {
        camera.zoom(factor);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    synchronized void cameraFling(double vx, double vy) {
        camera.fling(vx, vy);
        cameraChanged = true;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    synchronized void snapCameraToAxis() {
        camera.stopFling();
        camera.snapToAxis();
        cameraChanged = true;
        orbitalView.requestRender();
    }

    synchronized void requestScreenGrab(Handler handler) {
        screenGrabRequested = true;
        screenGrabHandler = handler;
        if (!orbital.color)
            orbitalView.requestRender();
    }

    // Render thread getter

    synchronized FrozenState freeze(double aspectRatio) {
        FrozenState fs = new FrozenState();

        boolean stillFlinging = camera.continueFling();
        if (stillFlinging && !orbital.color)
            orbitalView.requestRender();

        fs.inverseTransform = camera.computeInverseShaderTransform(aspectRatio);
        fs.orbital = orbital;
        fs.needToIntegrate = orbitalChanged || cameraChanged || stillFlinging;
        fs.screenGrabRequested = screenGrabRequested;
        fs.screenGrabHandler = screenGrabHandler;

        orbitalChanged = false;
        cameraChanged = false;
        screenGrabRequested = false;
        screenGrabHandler = null;

        return fs;
    }

    static class FrozenState {
        float[] inverseTransform;
        Orbital orbital;
        boolean needToIntegrate;
        boolean screenGrabRequested;
        Handler screenGrabHandler;
    }

    // Render thread error handling

    synchronized void reportRenderException(RuntimeException e) {
        Message.obtain(renderExceptionHandler, 0, e).sendToTarget();
    }
}
