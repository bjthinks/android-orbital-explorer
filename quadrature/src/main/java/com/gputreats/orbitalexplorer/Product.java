package com.gputreats.orbitalexplorer;

public class Product implements Function {

    private Function f;
    private Function g;

    public Product(Function ff, Function gg) {
        f = ff;
        g = gg;
    }

    @Override
    public double eval(double x) {
        return f.eval(x) * g.eval(x);
    }
}
