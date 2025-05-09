package com.gputreats.orbitalexplorer;

enum Romberg {
    ;

    // Integrate a function from 0 to +infinity
    static double integrate(Function f, double minLength) {
        int n = 1;
        double[] moreAccurateEstimate = new double[n];
        double stepSize = 1.0;
        moreAccurateEstimate[0] = trapezoidalEstimate(f, stepSize, minLength);

        do {
            ++n;
            double[] lessAccurateEstimate = moreAccurateEstimate;
            moreAccurateEstimate = new double[n];
            stepSize *= 0.5;
            moreAccurateEstimate[0] = trapezoidalEstimate(f, stepSize, minLength);
            double c = 1.0;
            for (int i = 1; i < n; ++i) {
                c *= 4.0;
                moreAccurateEstimate[i]
                        =  c  / (c - 1.0) * moreAccurateEstimate[i - 1]
                        - 1.0 / (c - 1.0) * lessAccurateEstimate[i - 1];
            }
        } while (n < 5);

        return moreAccurateEstimate[n - 1];
    }

    // Estimate the integral of f from 0 to +infinity using trapezoids of width stepSize.
    // Assumes f goes to zero at infinity, otherwise will not terminate.
    // Heuristically guesses when to stop by noticing when the accumulated total
    // doesn't change for multiple trapezoids in a row.
    public static double trapezoidalEstimate(Function f, double stepSize, double minLength) {
        double nextResult = 0.5 * f.eval(0.0);
        int i = 0;
        int numberOfConsecutiveIdenticalResults = 0;

        do {
            double priorResult = nextResult;
            nextResult += f.eval(stepSize * (double) ++i);
            if (priorResult == nextResult)
                ++numberOfConsecutiveIdenticalResults;
            else
                numberOfConsecutiveIdenticalResults = 0;
        } while (stepSize * (double) i < minLength || numberOfConsecutiveIdenticalResults < 5);

        return nextResult * stepSize;
    }
}
