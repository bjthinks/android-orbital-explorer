package com.example;

import com.transvect.orbitalexplorer.Function;
import com.transvect.orbitalexplorer.MyMath;
import com.transvect.orbitalexplorer.Polynomial;

public class WeightFunction implements Function {
    private double exponentialConstant;
    private Polynomial polynomialInR;
    private double distanceFromOrigin;

    public WeightFunction(double exponentialConstant_,
                          Polynomial polynomialInR_,
                          double distanceFromOrigin_) {
        exponentialConstant = exponentialConstant_;
        polynomialInR = polynomialInR_;
        distanceFromOrigin = distanceFromOrigin_;
    }

    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = Math.exp(exponentialConstant * r) * polynomialInR.eval(r);

        // Wave function is squared
        return value * value;
    }
}
