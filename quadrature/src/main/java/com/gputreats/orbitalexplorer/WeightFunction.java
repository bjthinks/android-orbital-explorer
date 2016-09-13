package com.gputreats.orbitalexplorer;

class WeightFunction implements Function {
    private final double exponentialConstant;
    private final Function polynomialInR;
    private final double distanceFromOrigin;

    WeightFunction(double inExponentialConstant,
                   Function inPolynomialInR,
                   double inDistanceFromOrigin) {
        exponentialConstant = inExponentialConstant;
        polynomialInR = inPolynomialInR;
        distanceFromOrigin = inDistanceFromOrigin;
    }

    @Override
    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = Math.exp(exponentialConstant * r) * polynomialInR.eval(r);

        // Wave function is squared
        return value * value;
    }
}
