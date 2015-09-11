package com.transvect.orbitalexplorer;

/**
 * Created by bwj on 9/11/15.
 */
public class Controller {
    public synchronized void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }

    public synchronized Quaternion getTotalRotation() {
        return mTotalRotation;
    }

    private static Quaternion mTotalRotation = new Quaternion(1.0);
}
