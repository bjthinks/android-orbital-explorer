package com.transvect.orbitalexplorer;

public class Square implements Function {
    Function mF;

    public Square(Function f) {
        mF = f;
    }

    @Override
    public double eval(double x) {
        double fx = mF.eval(x);
        return fx * fx;
    }
}
