package com.transvect.orbitalexplorer;

public class Power implements Function {

    private double p;

    public Power(int p_) {
        p = p_;
    }

    @Override
    public double eval(double x) {
        return Math.pow(x, p);
    }
}
