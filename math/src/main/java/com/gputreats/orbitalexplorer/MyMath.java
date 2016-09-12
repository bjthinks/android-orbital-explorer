package com.gputreats.orbitalexplorer;

final class MyMath {

    private MyMath() {}

    static double binomial(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

    static double factorial(int n) {
        if (n < 0)
            throw new IllegalArgumentException("Factorial of negative number");

        double result = 1.0;

        for (int i = 2; i <= n; ++i)
            result *= (double) i;

        return result;
    }

    // A faster version of Math.pow() when the exponent is a small positive integer
    static double fastpow(double base, int exponent) {
        double b = base;
        int e = exponent;

        if (e < 0)
            throw new IllegalArgumentException("fastpow with a negative exponent");

        double r = 1.0;

        while (e > 0) {
            if ((e & 1) != 0)
                r *= b;
            e >>= 1;
            b *= b;
        }

        return r;
    }
}
