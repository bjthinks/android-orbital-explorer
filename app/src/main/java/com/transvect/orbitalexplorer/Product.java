package com.transvect.orbitalexplorer;

public class Product implements Function {
    Function mF;
    Function mG;

    public Product(Function f, Function g) {
        mF = f;
        mG = g;
    }

    @Override
    public double eval(double x) {
        return mF.eval(x) * mG.eval(x);
    }
}
