package com.transvect.orbitalexplorer;

public class Squared implements Function {
    Function mF;

    public Squared(Function f) {
        mF = f;
    }

    @Override
    public double eval(double x) {
        double fx = mF.eval(x);
        return fx * fx;
    }
}
