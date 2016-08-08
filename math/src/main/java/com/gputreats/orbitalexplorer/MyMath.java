package com.gputreats.orbitalexplorer;

public final class MyMath {

    private MyMath() {}

    public static double binomial(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

    public static double factorial(int n) {
        if (n < 0)
            throw new IllegalArgumentException("Factorial of negative number");

        double result = 1;

        for (int i = 2; i <= n; ++i)
            result *= i;

        return result;
    }

    // A faster version of Math.pow() when the exponent is a small positive integer
    public static double fastpow(double base, int exponent) {
        if (exponent < 0)
            throw new IllegalArgumentException("fastpow with a negative exponent");

        double result = 1.0;

        while (exponent > 0) {
            if ((exponent & 1) != 0)
                result *= base;
            exponent >>= 1;
            base *= base;
        }

        return result;
    }

    // A misc data manipulation
    protected static float[] functionToBuffer2(Function f, double start, double end, int N) {
        float data[] = new float[2 * N];
        double stepSize = (end - start) / (N - 1);
        double x = start;
        float value = (float) f.eval(x);
        for (int i = 0; i < N; ++i) {
            data[2 * i] = value;
            x += stepSize;
            value = (float) f.eval(x);
            data[2 * i + 1] = value;
        }
        return data;
    }
}
