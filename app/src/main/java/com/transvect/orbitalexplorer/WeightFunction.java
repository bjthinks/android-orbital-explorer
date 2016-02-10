package com.transvect.orbitalexplorer;

public class WeightFunction implements Function {
    private double exponentialConstant;
    private int twicePowerOfR;
    private double mDistanceFromOrigin;

    public WeightFunction(double exponentialConstant_,
                          int powerOfR_,
                          double distanceFromOrigin) {
        exponentialConstant = exponentialConstant_;
        twicePowerOfR = 2 * powerOfR_;
        mDistanceFromOrigin = distanceFromOrigin;
    }

    public double eval(double x) {

        double r = Math.sqrt(mDistanceFromOrigin * mDistanceFromOrigin + x * x);

        // Multiply by 2 because the wave function is squared
        double value = Math.exp(2.0 * exponentialConstant * r);

        // Multiply by 2 because the wave function is squared
        value *= MyMath.fastpow(r, twicePowerOfR);

        return value;
    }
}
