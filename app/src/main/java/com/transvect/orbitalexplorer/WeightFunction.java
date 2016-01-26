package com.transvect.orbitalexplorer;

public class WeightFunction implements Function {
    private double exponentialConstant;
    private int powerOfR;
    private double mDistanceFromOrigin;

    public WeightFunction(double exponentialConstant_,
                          int powerOfR_,
                          double distanceFromOrigin) {
        exponentialConstant = exponentialConstant_;
        powerOfR = powerOfR_;
        mDistanceFromOrigin = distanceFromOrigin;
    }

    public double eval(double x) {
        if (x < 0.0)
            return 0.0;

        double r = Math.sqrt(mDistanceFromOrigin * mDistanceFromOrigin + x * x);
        // Multiply by 2 because the wave function is squared
        double value = Math.exp(2.0 * exponentialConstant * r);
        // Multiply by 2 because the wave function is squared
        value *= Math.pow(r, 2.0 * powerOfR);

        if (x == 0.0)
            value *= 0.5;

        return value;
    }
}
