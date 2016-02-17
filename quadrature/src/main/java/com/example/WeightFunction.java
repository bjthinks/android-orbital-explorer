package com.example;

import com.transvect.orbitalexplorer.Function;
import com.transvect.orbitalexplorer.MyMath;

public class WeightFunction implements Function {
    private double exponentialConstant;
    private int powerOfR;
    private double distanceFromOrigin;
    private double maximumRadius;

    public WeightFunction(double exponentialConstant_,
                          int powerOfR_,
                          double distanceFromOrigin_,
                          double maximumRadius_) {
        exponentialConstant = exponentialConstant_;
        powerOfR = powerOfR_;
        distanceFromOrigin = distanceFromOrigin_;
        maximumRadius = maximumRadius_;
    }

    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = Math.exp(exponentialConstant * r) * MyMath.fastpow(r, powerOfR);
        value /= 1.0 + Math.exp(2.0 * (r - maximumRadius));

        // Wave function is squared
        return value * value;
    }
}
