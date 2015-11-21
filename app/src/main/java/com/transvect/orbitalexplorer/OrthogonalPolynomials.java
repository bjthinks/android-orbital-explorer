package com.transvect.orbitalexplorer;

import android.util.Log;

public class OrthogonalPolynomials {
    private static final String TAG = "OrthogonalPolynomials";

    public OrthogonalPolynomials(Function w) {
        int n = 8;
        Polynomial[] foofoo = new Polynomial[n];
        foofoo[0] = new Polynomial(1.0);
        foofoo[1] = Polynomial.variable().subtract(MyMath.rombergIntegrate(
                new Product(Polynomial.variable(), w)) / MyMath.rombergIntegrate(w));
        for (int i = 2; i < n; ++i) {
            double B = MyMath.rombergIntegrate(new Product(new Product(Polynomial.variable(),
                    new Product(foofoo[i - 1], foofoo[i - 1])), w))
                    / MyMath.rombergIntegrate(new Product(new Product(foofoo[i - 1], foofoo[i - 1]), w));
            double A = MyMath.rombergIntegrate(new Product(new Product(foofoo[i - 1], foofoo[i - 1]), w))
                    / MyMath.rombergIntegrate(new Product(new Product(foofoo[i - 2], foofoo[i - 2]), w));
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
}
