package com.gputreats.orbitalexplorer;

public class WeightFunction implements Function {
    private final double exponentialConstant;
    private final Function polynomialInR;
    private final double distanceFromOrigin;

    public WeightFunction(double exponentialConstant_,
                          Function polynomialInR_,
                          double distanceFromOrigin_) {
        exponentialConstant = exponentialConstant_;
        polynomialInR = polynomialInR_;
        distanceFromOrigin = distanceFromOrigin_;
    }

    @Override
    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = Math.exp(exponentialConstant * r) * polynomialInR.eval(r);

        // Wave function is squared
        return value * value;
    }
}
