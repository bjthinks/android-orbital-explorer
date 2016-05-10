package com.gputreats.orbitalexplorer;

import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Camera implements Parcelable {

    static final double r2 = Math.sqrt(0.5);
    static final Quaternion alignedRotations[] = {
            new Quaternion( 1,  0,  0,  0),
            new Quaternion(-1,  0,  0,  0),
            new Quaternion( 0,  1,  0,  0),
            new Quaternion( 0, -1,  0,  0),
            new Quaternion( 0,  0,  1,  0),
            new Quaternion( 0,  0, -1,  0),
            new Quaternion( 0,  0,  0,  1),
            new Quaternion( 0,  0,  0, -1),
            new Quaternion( r2,  r2, 0, 0),
            new Quaternion( r2, -r2, 0, 0),
            new Quaternion(-r2,  r2, 0, 0),
            new Quaternion(-r2, -r2, 0, 0),
            new Quaternion( r2, 0,  r2, 0),
            new Quaternion( r2, 0, -r2, 0),
            new Quaternion(-r2, 0,  r2, 0),
            new Quaternion(-r2, 0, -r2, 0),
            new Quaternion( r2, 0, 0,  r2),
            new Quaternion( r2, 0, 0, -r2),
            new Quaternion(-r2, 0, 0,  r2),
            new Quaternion(-r2, 0, 0, -r2),
            new Quaternion(0,  r2,  r2, 0),
            new Quaternion(0,  r2, -r2, 0),
            new Quaternion(0, -r2,  r2, 0),
            new Quaternion(0, -r2, -r2, 0),
            new Quaternion(0,  r2, 0,  r2),
            new Quaternion(0,  r2, 0, -r2),
            new Quaternion(0, -r2, 0,  r2),
            new Quaternion(0, -r2, 0, -r2),
            new Quaternion(0, 0,  r2,  r2),
            new Quaternion(0, 0,  r2, -r2),
            new Quaternion(0, 0, -r2,  r2),
            new Quaternion(0, 0, -r2, -r2),
            new Quaternion( .5,  .5,  .5,  .5),
            new Quaternion( .5,  .5,  .5, -.5),
            new Quaternion( .5,  .5, -.5,  .5),
            new Quaternion( .5,  .5, -.5, -.5),
            new Quaternion( .5, -.5,  .5,  .5),
            new Quaternion( .5, -.5,  .5, -.5),
            new Quaternion( .5, -.5, -.5,  .5),
            new Quaternion( .5, -.5, -.5, -.5),
            new Quaternion(-.5,  .5,  .5,  .5),
            new Quaternion(-.5,  .5,  .5, -.5),
            new Quaternion(-.5,  .5, -.5,  .5),
            new Quaternion(-.5,  .5, -.5, -.5),
            new Quaternion(-.5, -.5,  .5,  .5),
            new Quaternion(-.5, -.5,  .5, -.5),
            new Quaternion(-.5, -.5, -.5,  .5),
            new Quaternion(-.5, -.5, -.5, -.5)
    };

    public Camera() {
    }

    public Camera(double cameraDistance_, Quaternion totalRotation_) {
        cameraDistance = cameraDistance_;
        totalRotation = totalRotation_;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(cameraDistance);
        out.writeParcelable(totalRotation, flags);
    }

    public static final Parcelable.Creator<Camera> CREATOR
            = new Parcelable.Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in.readDouble(),
                    (Quaternion) in.readParcelable(Quaternion.class.getClassLoader()));
        }

        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    private double cameraDistance = 60.0;
    private static final double MIN_CAMERA_DISTANCE = 1.5;
    private static final double MAX_CAMERA_DISTANCE = 280.0;

    // Two finger zoom by an incremental size ratio of f
    public void zoom(double f) {
        cameraDistance /= f;
        if (cameraDistance < MIN_CAMERA_DISTANCE)
            cameraDistance = MIN_CAMERA_DISTANCE;
        if (cameraDistance > MAX_CAMERA_DISTANCE)
            cameraDistance = MAX_CAMERA_DISTANCE;
    }

    private Quaternion totalRotation = Quaternion.rotation(Math.PI / 2.0, X_HAT);
    private static final Vector3 X_HAT = new Vector3(1, 0, 0);
    private static final Vector3 Y_HAT = new Vector3(0, 1, 0);
    private static final Vector3 Z_HAT = new Vector3(0, 0, 1);

    // One finger drag by an increment of (x,y) pixels
    // x and y are multiples of the (mean) screen size
    public void drag(double x, double y) {
        // Finger moves right --> positive rotation about y axis
        Quaternion y_rotation = Quaternion.rotation(Math.PI * x, Y_HAT);
        // Finger moves up --> negative rotation about x axis
        Quaternion x_rotation = Quaternion.rotation(-Math.PI * y, X_HAT);
        // total = normalize(x_rot * y_rot * total)
        totalRotation = x_rotation.multiply(y_rotation).multiply(totalRotation);
        totalRotation = totalRotation.normalize();
    }

    // Two finger twist by an angle increment of theta
    public void twist(double theta) {
        Quaternion z_rotation = Quaternion.rotation(theta, Z_HAT);
        totalRotation = z_rotation.multiply(totalRotation);
        totalRotation = totalRotation.normalize();
    }

    private Vector2 flingVelocity = new Vector2(0.0, 0.0);
    private boolean stillFlinging = false;
    private long lastFlingTime;
    // Maximum half-turns per second
    private static final double MAX_FLING_SPEED = 6.0;
    // Fraction of total speed lost per second
    private static final double FLING_SLOWDOWN_LINEAR = 0.5;
    // Seconds before stopping, if there were no linear slowdown
    private static final double MAX_FLING_TIME = 5.0;
    // Absolute amount of speed lost per second
    private static final double FLING_SLOWDOWN_CONSTANT = MAX_FLING_SPEED / MAX_FLING_TIME;

    public void fling(double x, double y) {
        // x and y are multiples of the mean screen size per second
        flingVelocity = new Vector2(x, y);
        double flingSpeed = flingVelocity.norm();
        if (flingSpeed > MAX_FLING_SPEED)
            flingVelocity = flingVelocity.multiply(MAX_FLING_SPEED / flingSpeed);
        stillFlinging = true;
        lastFlingTime = System.currentTimeMillis();
    }

    public boolean stopFling() {
        flingVelocity = new Vector2(0.0, 0.0);
        boolean r = stillFlinging;
        stillFlinging = false;
        return r;
    }

    public boolean continueFling() {
        if (stillFlinging) {
            long now = System.currentTimeMillis();
            long deltaMillis = now - lastFlingTime;
            double deltaTime = ((double) deltaMillis) / 1000.0;
            lastFlingTime = now;

            flingVelocity = flingVelocity.multiply(1 - Math.min(1.0, FLING_SLOWDOWN_LINEAR * deltaTime));
            if (flingVelocity.norm() < FLING_SLOWDOWN_CONSTANT * deltaTime) {
                stopFling();
            } else {
                Vector2 flingDirection = flingVelocity.normalize();
                Vector2 velocityReduction = flingDirection.multiply(FLING_SLOWDOWN_CONSTANT * deltaTime);
                flingVelocity = flingVelocity.subtract(velocityReduction);
                drag(flingVelocity.getX() * deltaTime, flingVelocity.getY() * deltaTime);
            }
        }

        return stillFlinging;
    }

    public void snapToAxis() {
        int best = -1;
        double bestDistance = 1e9;
        for (int i = 0; i < alignedRotations.length; ++i) {
            double d = totalRotation.dist(alignedRotations[i]);
            if (d < bestDistance) {
                bestDistance = d;
                best = i;
            }
        }
        if (best >= 0)
            totalRotation = alignedRotations[best];
    }

    public float[] computeInverseShaderTransform(double aspectRatio) {
        float ratio = (float) Math.sqrt(aspectRatio);
        float near = (float) cameraDistance;
        float far = (float) (cameraDistance + 1.0);
        float leftRight = near * ratio;
        float bottomTop = near / ratio;
        float[] projectionMatrix = new float[16];
        Matrix.frustumM(projectionMatrix, 0,
                -leftRight, leftRight,
                -bottomTop, bottomTop,
                near, far);

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, (float) (-cameraDistance), 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        float[] viewProjMatrix = new float[16];
        Matrix.multiplyMM(viewProjMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        float[] cameraRotation = totalRotation.asRotationMatrix();

        float[] shaderTransform = new float[16];
        Matrix.multiplyMM(shaderTransform, 0, viewProjMatrix, 0, cameraRotation, 0);

        // Samsung Galaxy S5 can't invert 4x4 matrices correctly in the OpenGL driver,
        // so we make the CPU do the inverse instead.
        float[] inverseTransform = new float[16];
        Matrix.invertM(inverseTransform, 0, shaderTransform, 0);

        return inverseTransform;
    }

    public double getCameraDistance() {
        return cameraDistance;
    }
}
