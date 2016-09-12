package com.gputreats.orbitalexplorer;

final class GeneralizedLaguerrePolynomial {

    private GeneralizedLaguerrePolynomial() {}

    /**
     * GeneralizedLaguerrePolynomial.generate(n, a) gives the polynomial
     * L_n^a(x), as per the definition on Wikipedia.
     */
    static Polynomial generate(int n, int a) {
        Polynomial result = new Polynomial();
        for (int i = 0; i <= n; ++i) {
            double coeff = MyMath.binomial(n + a, n - i) / MyMath.factorial(i);
            if ((i & 1) == 1)
                coeff = -coeff;
            result = result.add(Polynomial.variableToThe(i).multiply(coeff));
        }
        return result;
    }
}
