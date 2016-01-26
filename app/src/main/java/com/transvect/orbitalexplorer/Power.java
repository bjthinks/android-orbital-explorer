package com.transvect.orbitalexplorer;

public class Power implements Function {

    private int p;

    public Power(int p_) {
        p = p_;
    }

    @Override
    public double eval(double x) {
        return MyMath.ipow(x, p);
    }
}
