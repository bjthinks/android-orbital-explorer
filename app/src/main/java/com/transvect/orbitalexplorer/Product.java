package com.transvect.orbitalexplorer;

public class Product implements Function {
    Function f;
    Function g;

    public Product(Function f_, Function g_) {
        f = f_;
        g = g_;
    }

    @Override
    public double eval(double x) {
        return f.eval(x) * g.eval(x);
    }
}
