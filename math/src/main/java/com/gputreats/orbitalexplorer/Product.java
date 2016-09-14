package com.gputreats.orbitalexplorer;

class Product implements Function {

    private final Function f, g;

    Product(Function ff, Function gg) {
        f = ff;
        g = gg;
    }

    @Override
    public double eval(double x) {
        return f.eval(x) * g.eval(x);
    }
}
