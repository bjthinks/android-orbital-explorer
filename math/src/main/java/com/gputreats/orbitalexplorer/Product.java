package com.gputreats.orbitalexplorer;

class Product implements Function {

    private final Function f, g;

    Product(Function inF, Function inG) {
        f = inF;
        g = inG;
    }

    @Override
    public double eval(double x) {
        return f.eval(x) * g.eval(x);
    }
}
