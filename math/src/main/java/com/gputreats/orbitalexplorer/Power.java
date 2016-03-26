package com.gputreats.orbitalexplorer;

public class Power implements Function {

    private int p;

    public Power(int pp) {
        p = pp;
    }

    @Override
    public double eval(double x) {
        return MyMath.fastpow(x, p);
    }
}
