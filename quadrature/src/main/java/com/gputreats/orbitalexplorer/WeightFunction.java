package com.gputreats.orbitalexplorer;

class WeightFunction implements Function {
    private final double constantFactors;
    private final double exponentialConstant;
    private final Function polynomialInR;
    private final double distanceFromOrigin;

    WeightFunction(double inConstantFactors,
                   double inExponentialConstant,
                   Function inPolynomialInR,
                   double inDistanceFromOrigin) {
        constantFactors = inConstantFactors;
        exponentialConstant = inExponentialConstant;
        polynomialInR = inPolynomialInR;
        distanceFromOrigin = inDistanceFromOrigin;
    }

    @Override
    public double eval(double x) {

        double r = Math.sqrt(distanceFromOrigin * distanceFromOrigin + x * x);
        double value = constantFactors * Math.exp(exponentialConstant * r) * polynomialInR.eval(r);

        // Wave function is squared
        return value * value;
    }
}
