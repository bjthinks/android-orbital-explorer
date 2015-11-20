package com.transvect.orbitalexplorer;

import android.util.Log;

public final class MyMath {
    private static final String TAG = "MyMath";

    private MyMath() {}

    /**
     * MyMath.legendrePolynomial(n) gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n],
     * as per the definition on Wikipedia.
     */
    public static Polynomial legendrePolynomial(int L) {
        Polynomial result = Polynomial.variableToThe(2).subtract(1.0).pow(L);

        for (int i = 0; i < L; ++i)
            result = result.derivative().multiply(1.0 / (2.0 * (double) (i + 1)));

        return result;
    }

    /**
     * MyMath.generalizedLaguerrePolynomial(n, a) gives the polynomial
     * L_n^a(x), as per the definition on Wikipedia.
     */
    public static Polynomial generalizedLaguerrePolynomial(int n, int a) {
        Polynomial result = new Polynomial();
        for (int i = 0; i <= n; ++i) {
            double coeff = binomial(n + a, n - i) / factorial(i);
            if (i % 2 == 1)
                coeff = -coeff;
            result = result.add(Polynomial.variableToThe(i).multiply(coeff));
        }
        return result;
    }

    public static double binomial(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

    public static double factorial(int n) {
        if (n < 0)
            Log.w(TAG, "Factorial of negative number");
        double result = 1;
        for (int i = 2; i <= n; ++i)
            result *= i;
        return result;
    }

    public static double rombergIntegrate(Function f) {
        Log.d(TAG, "----- Begin Romberg Integration -----");

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
            Log.d(TAG, "Romberg estimate: " + moreAccurateEstimate[n - 1]);
        } while (moreAccurateEstimate[n - 1] != moreAccurateEstimate[n-2]);

        Log.d(TAG, "------- End Romberg Integration -------");

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
        if (previousResult == nextResult)
            Log.w(TAG, "Integration step size too large");
        while (previousResult != nextResult) {
            previousResult = nextResult;
            iMax += stepsAtATime;
            for (; i <= iMax; ++i)
                nextResult += f.eval(-stepSize * (double) i) + f.eval(stepSize * (double) i);
        }
        Log.d(TAG, "                                        "
                + iMax + " steps, result is " + nextResult * stepSize);
        return nextResult * stepSize;
    }
}