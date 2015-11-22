package com.transvect.orbitalexplorer;

import android.util.Log;

public class OrthogonalPolynomials {
    private static final String TAG = "OrthogonalPolynomials";

    Function mW;

    public OrthogonalPolynomials(Function w) {
        mW = w;
        int n = 8;
        Polynomial x = Polynomial.variable();
        Polynomial[] basis = new Polynomial[n];
        basis[0] = new Polynomial(1.0);
        basis[1] = x.subtract(innerProduct(x.multiply(basis[0]), basis[0]) / normSquared(basis[0]));
        for (int i = 2; i < n; ++i) {
            double B = innerProduct(new Product(x, basis[i - 1]), basis[i - 1])
                    / normSquared(basis[i - 1]);
            double A = normSquared(basis[i - 1]) / normSquared(basis[i - 2]);
            basis[i] = basis[i - 1].multiply(x.subtract(B)).subtract(basis[i - 2].multiply(A));
            Log.d(TAG, i + ":       " + basis[i].toString());
            double[] roots = MyMath.solvePolynomial(basis[i]);
            String rootsString = "";
            for (int j = 0; j < roots.length; ++j)
                rootsString += " " + roots[j];
            Log.d(TAG, i + " roots:" + rootsString);
        }
    }

    private double normSquared(Function f) {
        Function f2 = new Square(f);
        Function integrand = new Product(f2, mW);
        return MyMath.rombergIntegrate(integrand);
    }

    private double innerProduct(Function f, Function g) {
        Function fg = new Product(f, g);
        Function integrand = new Product(fg, mW);
        return MyMath.rombergIntegrate(integrand);
    }
}
