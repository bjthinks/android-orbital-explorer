package com.transvect.orbitalexplorer;

public class Squared implements Function {

    Function f;

    public Squared(Function f_) {
        f = f_;
    }

    @Override
    public double eval(double x) {
        double fx = f.eval(x);
        return fx * fx;
    }
}
