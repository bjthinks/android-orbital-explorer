package com.example;

import com.transvect.orbitalexplorer.Function;

public class Composition implements Function {

    private Function f;
    private Function g;

    public Composition(Function ff, Function gg) {
        f = ff;
        g = gg;
    }

    @Override
    public double eval(double x) {
        return f.eval(g.eval(x));
    }
}
