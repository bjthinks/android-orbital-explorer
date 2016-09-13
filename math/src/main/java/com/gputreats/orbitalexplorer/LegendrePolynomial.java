package com.gputreats.orbitalexplorer;

enum LegendrePolynomial {
    ;

    /**
     * LegendrePolynomial.generate(n) gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n], as per the definition on Wikipedia.
     */
    public static Polynomial generate(int L) {
        Polynomial result = Polynomial.variableToThe(2).subtract(1.0).pow(L);

        for (int i = 0; i < L; ++i)
            result = result.derivative().multiply(1.0 / (2.0 * (double) (i + 1)));

        return result;
    }
}
