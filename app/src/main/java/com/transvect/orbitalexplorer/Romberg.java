package com.transvect.orbitalexplorer;

import android.util.Log;

public final class Romberg {

    private static final String TAG = "Romberg";

    private Romberg() {}

    public static double rombergIntegrate(Function f) {
        double[] lessAccurateEstimate;
        int n = 1;
        double[] moreAccurateEstimate = new double[n];
        moreAccurateEstimate[0] = trapezoidalEstimate(f, 1.0);

        do {
            ++n;
            lessAccurateEstimate = moreAccurateEstimate;
            moreAccurateEstimate = new double[n];
            moreAccurateEstimate[0] = trapezoidalEstimate(f,
                    Math.pow(0.5, (double) (n - 1)));
            for (int i = 1; i < n; ++i) {
                double c = Math.pow(4.0, (double) i);
                moreAccurateEstimate[i] = c / (c - 1.0) * moreAccurateEstimate[i - 1]
                        - 1.0 / (c - 1.0) * lessAccurateEstimate[i - 1];
            }
        } while (moreAccurateEstimate[n - 1] != moreAccurateEstimate[n-2]);

        return moreAccurateEstimate[n - 1];
    }

    // Estimate the integral of f using trapezoids of width stepSize
    public static double trapezoidalEstimate(Function f, double stepSize) {
        double previousResult = f.eval(0.0);
        double nextResult = previousResult;
        int i = 1;
        int stepsAtATime = 5;
        int iMax = stepsAtATime;
        for (; i <= iMax; ++i)
            nextResult += f.eval(-stepSize * (double) i) + f.eval(stepSize * (double) i);
        // TODO this check is not right
        if (previousResult == nextResult && previousResult != 0.0)
            Log.w(TAG, "Integration step size too large");
        while (previousResult != nextResult) {
            previousResult = nextResult;
            iMax += stepsAtATime;
            for (; i <= iMax; ++i)
                nextResult += f.eval(-stepSize * (double) i) + f.eval(stepSize * (double) i);
        }
        return nextResult * stepSize;
    }

}
