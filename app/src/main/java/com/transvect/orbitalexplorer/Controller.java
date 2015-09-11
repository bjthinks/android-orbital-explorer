package com.transvect.orbitalexplorer;

/**
 * Created by bwj on 9/11/15.
 */
public class Controller {
    public static Quaternion getTotalRotation() {
        return mTotalRotation;
    }

    public static void setTotalRotation(Quaternion mTotalRotation) {
        Controller.mTotalRotation = mTotalRotation;
    }

    private static Quaternion mTotalRotation = new Quaternion(1.0);
}
