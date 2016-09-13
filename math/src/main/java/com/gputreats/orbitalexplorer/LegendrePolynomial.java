package com.gputreats.orbitalexplorer;

enum LegendrePolynomial {
    ;

    /**
     * LegendrePolynomial.generate(n) gives the polynomial
     * P_n(x) = (2^n n!)^-1 d^n/dx^n [(x^2-1)^n], as per the definition on Wikipedia.
     */
    public static Polynomial generate(int qL) {
        Polynomial result = Polynomial.variableToThe(2).subtract(1.0).pow(qL);

        for (int i = 0; i < qL; ++i)
            result = result.derivative().multiply(1.0 / (double) (2 * (i + 1)));

        return result;
    }
}
