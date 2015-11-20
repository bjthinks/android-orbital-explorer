package com.transvect.orbitalexplorer;

import android.util.Log;

public class OrthogonalPolynomials {
    private static final String TAG = "OrthogonalPolynomials";

    public OrthogonalPolynomials() {
        int n = 5;
        Polynomial[] foofoo = new Polynomial[n];
        Barbar barbar = new Barbar();
        for (int i = 0; i < n; ++i) {
            foofoo[i] = Polynomial.variableToThe(i);
            for (int j = 0; j < i; ++j)
                foofoo[i] = foofoo[i].subtract(foofoo[j].multiply(
                        MyMath.rombergIntegrate(new Product(barbar,
                                new Product(foofoo[i], foofoo[j])))));
            foofoo[i] = foofoo[i].multiply(1.0 / Math.sqrt(
                    MyMath.rombergIntegrate(new Product(barbar,
                            new Product(foofoo[i], foofoo[i])))));
            Log.d(TAG, i + ":   " + foofoo[i].toString());
        }
    }
    private class Barbar implements Function {
        public double eval(double x) {
            return Math.exp(-Math.abs(x));
        }
    }
}
