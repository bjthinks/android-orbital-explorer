package com.transvect.orbitalexplorer;

public class WeightFunction implements Function {
    private double exponentialConstant;
    private int powerOfR;
    private double distanceFromOrigin;

    public WeightFunction(double exponentialConstant_,
                          int powerOfR_,
                          double distanceFromOrigin_) {
        exponentialConstant = exponentialConstant_;
        powerOfR = powerOfR_;
        distanceFromOrigin = distanceFromOrigin_;
    }

    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = Math.exp(exponentialConstant * r) * MyMath.fastpow(r, powerOfR);

        // Wave function is squared
        return value * value;
    }
}
