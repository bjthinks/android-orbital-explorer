package com.transvect.orbitalexplorer;

import android.util.Log;

public class OrthogonalPolynomials {
    private static final String TAG = "OrthogonalPolynomials";

    Function w2;

    public OrthogonalPolynomials(Function w) {
        w2 = w; //new Product(w, w);
        int n = 8;
        Polynomial[] foofoo = new Polynomial[n];
        foofoo[0] = new Polynomial(1.0);
        foofoo[1] = Polynomial.variable().subtract(MyMath.rombergIntegrate(
                new Product(Polynomial.variable(), w)) / MyMath.rombergIntegrate(w));
        for (int i = 2; i < n; ++i) {
            double B = innerProduct(new Product(Polynomial.variable(), foofoo[i - 1]),
                    foofoo[i - 1]) / normSquared(foofoo[i - 1]);
            double A = normSquared(foofoo[i - 1]) / normSquared(foofoo[i - 2]);
            foofoo[i] = foofoo[i - 1].multiply(Polynomial.variable().subtract(B))
                    .subtract(foofoo[i - 2].multiply(A));
            Log.d(TAG, i + ":       " + foofoo[i].toString());
            double[] roots = MyMath.solvePolynomial(foofoo[i]);
            String rootsString = "";
            for (int j = 0; j < roots.length; ++j)
                rootsString += " " + roots[j];
            Log.d(TAG, i + " roots:" + rootsString);
        }
    }

    private double normSquared(Function f) {
        Function f2 = new Square(f);
        Function integrand = new Product(f2, w2);
        return MyMath.rombergIntegrate(integrand);
    }

    private double innerProduct(Function f, Function g) {
        Function fg = new Product(f, g);
        Function integrand = new Product(fg, w2);
        return MyMath.rombergIntegrate(integrand);
    }
}
