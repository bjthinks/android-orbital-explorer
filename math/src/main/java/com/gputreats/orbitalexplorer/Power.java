package com.gputreats.orbitalexplorer;

class Power implements Function {

    private final Function base;

    private final int power;

    Power(int inPower) {
        base = Polynomial.variableToThe(1);
        power = inPower;
    }

    Power(Function inBase, int inPower) {
        base = inBase;
        power = inPower;
    }

    @Override
    public double eval(double x) {
        return MyMath.fastpow(base.eval(x), power);
    }
}
