package com.transvect.orbitalexplorer;

/**
 * Created by bwj on 9/11/15.
 */
public class Controller {

    // TODO save these as preferences
    private static double mCameraDistance = 3.0;
    private static Quaternion mTotalRotation = new Quaternion(1.0);

    public synchronized void scaleBy(double f) {
        mCameraDistance /= f;
        if (mCameraDistance > 10.0)
            mCameraDistance = 10.0;
        if (mCameraDistance < 2.0)
            mCameraDistance = 2.0;
    }

    public synchronized void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }

    public synchronized double getCameraDistance() {
        return mCameraDistance;
    }

    public synchronized Quaternion getTotalRotation() {
        return mTotalRotation;
    }
}
