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

        Log.d(TAG, "Romberg integration finished in " + n + " rounds");

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

    // Assuming f has only simple real roots, find them all.
    public static double[] solvePolynomial(Polynomial f) {
        double[] roots;
        if (f.degree() < 1) {
            roots = new double[0];
        } else if (f.degree() == 1) {
            roots = new double[1];
            roots[0] = -f.coefficient(0) / f.coefficient(1);
        } else {
            roots = new double[f.degree()];
            double[] rootsOfDerivative = solvePolynomial(f.derivative());
            double left = searchForSignChange(f, rootsOfDerivative[0], -1.0);
            roots[0] = bisect(f, left, rootsOfDerivative[0]);
            for (int i = 1; i < f.degree() - 1; ++i)
                roots[i] = bisect(f, rootsOfDerivative[i - 1], rootsOfDerivative[i]);
            double right = searchForSignChange(f, rootsOfDerivative[f.degree() - 2], 1.0);
            roots[f.degree() - 1] = bisect(f, rootsOfDerivative[f.degree() - 2], right);
        }
        return roots;
    }

    private static double searchForSignChange(Polynomial f, double x, double deltax) {
        double fx = f.eval(x);
        if (fx == 0.0)
            Log.w(TAG, "searchForSignChange: f(x) already zero");
        double fdeltax = f.eval(x + deltax);
        while (fx > 0.0 && fdeltax > 0.0 || fx < 0.0 && fdeltax < 0.0) {
            deltax *= 2.0;
            if (Math.abs(deltax) > 1e10)
                Log.w(TAG, "searchForSignChange: search has gone awry");
            fdeltax = f.eval(x + deltax);
        }
        return x + deltax;
    }

    private static double bisect(Polynomial f, double left, double right) {
        // Is f(left) zero?
        double fLeft = f.eval(left);
        if (fLeft == 0.0)
            return left;

        // Is f(right) zero?
        double fRight = f.eval(right);
        if (fRight == 0.0)
            return right;

        // f(left) and f(right) better have opposite signs
        if (fLeft > 0.0 && fRight > 0.0 || fLeft < 0.0 && fRight < 0.0)
            // TODO throw exception here (and elsewhere for similar errors)
            Log.w(TAG, "bisect: f has same sign at left and right endpoints");

        // Compute mid. Is it the same as left or right?
        double mid = (left + right) / 2.0;
        if (mid == left || mid == right) {
            if (Math.abs(fLeft) <= Math.abs(fRight))
                return left;
            else
                return right;
        }

        // Is f(mid) zero?
        double fMid = f.eval(mid);
        if (fMid == 0.0)
            return mid;

        // mid is between left and right, f is nonzero at all three points,
        // and f has opposite signs at left and right. Recurse.
        if (fLeft > 0.0 && fMid > 0.0 || fLeft < 0.0 && fMid < 0.0)
            return bisect(f, mid, right);
        else
            return bisect(f, left, mid);
    }
}
