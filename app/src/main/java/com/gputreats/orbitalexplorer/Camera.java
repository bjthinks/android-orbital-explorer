package com.gputreats.orbitalexplorer;

import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

class Camera implements Parcelable {

    private static final double SQRT2 = Math.sqrt(0.5);
    private static final Quaternion[] ALIGNED_ROTATIONS = {
            new Quaternion( 1.0,  0.0,  0.0,  0.0),
            new Quaternion(-1.0,  0.0,  0.0,  0.0),
            new Quaternion( 0.0,  1.0,  0.0,  0.0),
            new Quaternion( 0.0, -1.0,  0.0,  0.0),
            new Quaternion( 0.0,  0.0,  1.0,  0.0),
            new Quaternion( 0.0,  0.0, -1.0,  0.0),
            new Quaternion( 0.0,  0.0,  0.0,  1.0),
            new Quaternion( 0.0,  0.0,  0.0, -1.0),
            new Quaternion( SQRT2,  SQRT2, 0.0, 0.0),
            new Quaternion( SQRT2, -SQRT2, 0.0, 0.0),
            new Quaternion(-SQRT2,  SQRT2, 0.0, 0.0),
            new Quaternion(-SQRT2, -SQRT2, 0.0, 0.0),
            new Quaternion( SQRT2, 0.0,  SQRT2, 0.0),
            new Quaternion( SQRT2, 0.0, -SQRT2, 0.0),
            new Quaternion(-SQRT2, 0.0,  SQRT2, 0.0),
            new Quaternion(-SQRT2, 0.0, -SQRT2, 0.0),
            new Quaternion( SQRT2, 0.0, 0.0,  SQRT2),
            new Quaternion( SQRT2, 0.0, 0.0, -SQRT2),
            new Quaternion(-SQRT2, 0.0, 0.0,  SQRT2),
            new Quaternion(-SQRT2, 0.0, 0.0, -SQRT2),
            new Quaternion(0.0,  SQRT2,  SQRT2, 0.0),
            new Quaternion(0.0,  SQRT2, -SQRT2, 0.0),
            new Quaternion(0.0, -SQRT2,  SQRT2, 0.0),
            new Quaternion(0.0, -SQRT2, -SQRT2, 0.0),
            new Quaternion(0.0,  SQRT2, 0.0,  SQRT2),
            new Quaternion(0.0,  SQRT2, 0.0, -SQRT2),
            new Quaternion(0.0, -SQRT2, 0.0,  SQRT2),
            new Quaternion(0.0, -SQRT2, 0.0, -SQRT2),
            new Quaternion(0.0, 0.0,  SQRT2,  SQRT2),
            new Quaternion(0.0, 0.0,  SQRT2, -SQRT2),
            new Quaternion(0.0, 0.0, -SQRT2,  SQRT2),
            new Quaternion(0.0, 0.0, -SQRT2, -SQRT2),
            new Quaternion( 0.5,  0.5,  0.5,  0.5),
            new Quaternion( 0.5,  0.5,  0.5, -0.5),
            new Quaternion( 0.5,  0.5, -0.5,  0.5),
            new Quaternion( 0.5,  0.5, -0.5, -0.5),
            new Quaternion( 0.5, -0.5,  0.5,  0.5),
            new Quaternion( 0.5, -0.5,  0.5, -0.5),
            new Quaternion( 0.5, -0.5, -0.5,  0.5),
            new Quaternion( 0.5, -0.5, -0.5, -0.5),
            new Quaternion(-0.5,  0.5,  0.5,  0.5),
            new Quaternion(-0.5,  0.5,  0.5, -0.5),
            new Quaternion(-0.5,  0.5, -0.5,  0.5),
            new Quaternion(-0.5,  0.5, -0.5, -0.5),
            new Quaternion(-0.5, -0.5,  0.5,  0.5),
            new Quaternion(-0.5, -0.5,  0.5, -0.5),
            new Quaternion(-0.5, -0.5, -0.5,  0.5),
            new Quaternion(-0.5, -0.5, -0.5, -0.5)
    };

    Camera() {}

    private Camera(double cameraDistanceIn, Quaternion totalRotationIn) {
        cameraDistance = cameraDistanceIn;
        totalRotation = totalRotationIn;
    }

    @SuppressWarnings("MethodReturnAlwaysConstant")
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(cameraDistance);
        dest.writeParcelable(totalRotation, flags);
    }

    @SuppressWarnings({"AnonymousInnerClassWithTooManyMethods", "AnonymousInnerClass"})
    public static final Parcelable.Creator<Camera> CREATOR
            = new Parcelable.Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel source) {
            return new Camera(source.readDouble(),
                    source.readParcelable(Quaternion.class.getClassLoader()));
        }
        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    private static final double MIN_CAMERA_DISTANCE = 1.5;
    private static final double MAX_CAMERA_DISTANCE = 280.0;
    private static final double INITIAL_CAMERA_DISTANCE = 60.0;

    private double cameraDistance = INITIAL_CAMERA_DISTANCE;

    // Two finger zoom by an incremental size ratio of f
    void zoom(double factor) {
        cameraDistance /= factor;
        if (cameraDistance < MIN_CAMERA_DISTANCE)
            cameraDistance = MIN_CAMERA_DISTANCE;
        if (cameraDistance > MAX_CAMERA_DISTANCE)
            cameraDistance = MAX_CAMERA_DISTANCE;
    }

    private static final Vector3 X_HAT = new Vector3(1.0, 0.0, 0.0);
    private static final Vector3 Y_HAT = new Vector3(0.0, 1.0, 0.0);
    private static final Vector3 Z_HAT = new Vector3(0.0, 0.0, 1.0);

    private static Quaternion rotation(double angle, Vector3 v) {
        double s = Math.sin(angle / 2.0);
        double c = Math.cos(angle / 2.0);
        return new Quaternion(c, v.normalize().multiply(s));
    }

    private static final Quaternion INITIAL_ROTATION = rotation(Math.PI / 2.0, X_HAT);

    private Quaternion totalRotation = INITIAL_ROTATION;

    // One finger drag by an increment of (x,y) pixels
    // x and y are multiples of the (mean) screen size
    void drag(double x, double y) {
        // Finger moves right --> positive rotation about y axis
        Quaternion yRotation = rotation(Math.PI * x, Y_HAT);
        // Finger moves up --> negative rotation about x axis
        Quaternion xRotation = rotation(-Math.PI * y, X_HAT);
        // total = normalize(x_rot * y_rot * total)
        totalRotation = xRotation.multiply(yRotation).multiply(totalRotation);
        totalRotation = totalRotation.normalize();
    }

    // Two finger twist by an angle increment of theta
    void twist(double theta) {
        Quaternion zRotation = rotation(theta, Z_HAT);
        totalRotation = zRotation.multiply(totalRotation);
        totalRotation = totalRotation.normalize();
    }

    private Vector2 flingVelocity = new Vector2(0.0, 0.0);
    private boolean stillFlinging;
    private long lastFlingTime;
    // Maximum half-turns per second
    private static final double MAX_FLING_SPEED = 6.0;
    // Fraction of total speed lost per second
    private static final double FLING_SLOWDOWN_LINEAR = 0.5;
    // Seconds before stopping, if there were no linear slowdown
    private static final double MAX_FLING_TIME = 5.0;
    // Absolute amount of speed lost per second
    private static final double FLING_SLOWDOWN_CONSTANT = MAX_FLING_SPEED / MAX_FLING_TIME;

    void fling(double x, double y) {
        // x and y are multiples of the mean screen size per second
        flingVelocity = new Vector2(x, y);
        double flingSpeed = flingVelocity.norm();
        if (flingSpeed > MAX_FLING_SPEED)
            flingVelocity = flingVelocity.multiply(MAX_FLING_SPEED / flingSpeed);
        stillFlinging = true;
        lastFlingTime = System.currentTimeMillis();
    }

    boolean stopFling() {
        flingVelocity = new Vector2(0.0, 0.0);
        boolean r = stillFlinging;
        stillFlinging = false;
        return r;
    }

    boolean continueFling() {
        if (stillFlinging) {
            long now = System.currentTimeMillis();
            long deltaMillis = now - lastFlingTime;
            double deltaTime = (double) deltaMillis / 1000.0;
            lastFlingTime = now;

            flingVelocity = flingVelocity.multiply(1.0
                    - Math.min(1.0, FLING_SLOWDOWN_LINEAR * deltaTime));
            if (flingVelocity.norm() < FLING_SLOWDOWN_CONSTANT * deltaTime) {
                stopFling();
            } else {
                Vector2 flingDirection = flingVelocity.normalize();
                Vector2 velocityReduction =
                        flingDirection.multiply(FLING_SLOWDOWN_CONSTANT * deltaTime);
                flingVelocity = flingVelocity.subtract(velocityReduction);
                drag(flingVelocity.getX() * deltaTime, flingVelocity.getY() * deltaTime);
            }
        }

        return stillFlinging;
    }

    void snapToAxis() {
        int best = -1;
        double bestDistance = 1.0e9;
        for (int i = 0; i < ALIGNED_ROTATIONS.length; ++i) {
            double d = totalRotation.dist(ALIGNED_ROTATIONS[i]);
            if (d < bestDistance) {
                bestDistance = d;
                best = i;
            }
        }
        if (best >= 0)
            totalRotation = ALIGNED_ROTATIONS[best];
    }

    float[] computeInverseShaderTransform(double aspectRatio) {
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
        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, (float) -cameraDistance,
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

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
}
