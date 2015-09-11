package com.transvect.orbitalexplorer;

/**
 * Created by bwj on 9/11/15.
 */
public class Controller {
    public void rotateBy(Quaternion r) {
        mTotalRotation = r.multiply(mTotalRotation);
    }

    public Quaternion getTotalRotation() {
        return mTotalRotation;
    }

    private static Quaternion mTotalRotation = new Quaternion(1.0);
}
