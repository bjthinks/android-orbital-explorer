package com.transvect.orbitalexplorer;

public final class MyMath {

    private MyMath() {}

    /**
     * MyMath.legendrePolynomial(n) gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n],
     * as per the definition on Wikipedia.
     */
    public static Polynomial legendrePolynomial(int L) {
        Polynomial result = Polynomial.variableToThe(2).subtract(1.0).pow(L);

        for (int i = 0; i < L; ++i)
            result = result.derivative().multiply(1.0 / (2.0 * (double) (i + 1)));

        return result;
    }

    /**
     * MyMath.generalizedLaguerrePolynomial(n, a) gives the polynomial
     * L_n^a(x), as per the definition on Wikipedia.
     */
    public static Polynomial generalizedLaguerrePolynomial(int n, int a) {
        Polynomial result = new Polynomial();
        for (int i = 0; i <= n; ++i) {
            double coeff = binomial(n + a, n - i) / factorial(i);
            if (i % 2 == 1)
                coeff = -coeff;
            result = result.add(Polynomial.variableToThe(i).multiply(coeff));
        }
        return result;
    }

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

    public static double ipow(double base, int exponent) {
        double result = 1.0;

        while (exponent > 0) {
            if ((exponent & 1) != 0)
                result *= base;
            exponent >>= 1;
            base *= base;
        }

        return result;
    }
}
