package com.gputreats.orbitalexplorer;

class Power implements Function {

    private final int power;

    Power(int inPower) {
        power = inPower;
    }

    @Override
    public double eval(double x) {
        return MyMath.fastpow(x, power);
    }
}
