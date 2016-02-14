package com.example;

import com.transvect.orbitalexplorer.Function;
import com.transvect.orbitalexplorer.MyMath;

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
