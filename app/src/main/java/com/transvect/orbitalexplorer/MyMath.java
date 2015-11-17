package com.transvect.orbitalexplorer;

import android.util.Log;

public final class MyMath {
    private static final String TAG = "MyMath";

    private MyMath() {}

    /**
     * MyMath.legendrePolynomial(n) gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n],
     * as per the definition on Wikipedia.
     */
    public static Polynomial legendrePolynomial(int L) {
        Polynomial result = new Polynomial(1.0);

        Polynomial xSquaredMinusOne =
                Polynomial.variable()
                        .multiply(Polynomial.variable())
                        .subtract(new Polynomial(1.0));

        for (int i = 0; i < L; ++i)
            result = result.multiply(xSquaredMinusOne);

        for (int i = 0; i < L; ++i)
            result = result.derivative().multiply(1.0 / (2.0 * (float) (i + 1)));

        return result;
    }

    /**
     * MyMath.generalizedLaguerrePolynomial(n, a) gives the polynomial
     * L_n^a(x), as per the definition on Wikipedia.
     */
    public static Polynomial generalizedLaguerrePolynomial(int n, int a) {
        Polynomial result = new Polynomial();
        Polynomial x = Polynomial.variable();
        Polynomial x_to_the_i = new Polynomial(1);
        double power_of_minus_one = 1;
        for (int i = 0; i <= n; ++i) {
            double coeff = power_of_minus_one * binomial(n + a, n - i) / factorial(i);
            result = result.add(x_to_the_i.multiply(coeff));
            x_to_the_i = x_to_the_i.multiply(x);
            power_of_minus_one *= -1;
        }
        return result;
    }

    public static double binomial(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

    public static double factorial(int n) {
        if (n < 0)
            Log.w(TAG, "Factorial of negative number");
        double result = 1;
        for (int i = 2; i <= n; ++i)
            result *= i;
        return result;
    }
}