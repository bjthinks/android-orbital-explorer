package com.transvect.orbitalexplorer;

import android.util.Log;

public class Polynomial {

    private double c[];

    Polynomial() {
        c = new double[0];
    }

    Polynomial(double cc) {
        if (cc == 0.)
            c = new double[0];
        else {
            c = new double[1];
            c[0] = cc;
        }
    }

    int degree() {
        return c.length - 1;
    }

    static Polynomial variable() {
        Polynomial x = new Polynomial();
        x.c = new double[2];
        x.c[0] = 0.;
        x.c[1] = 1.;
        return x;
    }

    public double eval(double x) {
        double result = 0.;
        for (int i = c.length - 1; i >= 0; --i) {
            result *= x;
            result += c[i];
        }
        return result;
    }

    /* Polynomial add(Polynomial rhs) {
    } */

    /* Polynomial subtract(Polynomial rhs) {
    } */

    /* Polynomial multiply(double c) {
    } */

    Polynomial negate() {
        Polynomial result = new Polynomial();
        result.c = new double[c.length];
        for (int i = 0; i < c.length; ++i) {
            result.c[i] = -c[i];
        }
        return result;
    }

    static void test() {
        String TAG = "Polynomial";
        Log.d(TAG, "Testing");
        TAG += " FAIL";

        Polynomial zero = new Polynomial();
        if (zero.degree() != -1)
            Log.w(TAG, "0.d()");
        Polynomial zero0 = new Polynomial(0.);
        if (zero0.degree() != -1)
            Log.w(TAG, "00.d()");
        Polynomial one = new Polynomial(1.);
        if (one.degree() != 0)
            Log.w(TAG, "1.d()");
        Polynomial x = Polynomial.variable();
        if (x.degree() != 1)
            Log.w(TAG, "x.d()");

        if (zero.eval(0) != 0.)
            Log.w(TAG, "z.e(0)");
        if (zero.eval(1) != 0.)
            Log.w(TAG, "z.e(1)");
        if (one.eval(0) != 1.)
            Log.w(TAG, "1.e(0)");
        if (one.eval(1) != 1.)
            Log.w(TAG, "1.e(1)");
        if (x.eval(0) != 0.)
            Log.w(TAG, "x.e(0)");
        if (x.eval(1) != 1.)
            Log.w(TAG, "x.e(1)");

        if (zero.negate().eval(0) != 0.)
            Log.w(TAG, "0.n().e(0)");
        if (zero.negate().eval(1) != 0.)
            Log.w(TAG, "0.n().e(1)");
        if (one.negate().eval(0) != -1.)
            Log.w(TAG, "1.n().e(0)");
        if (one.negate().eval(1) != -1.)
            Log.w(TAG, "1.n().e(1)");
        if (x.negate().eval(0) != 0.)
            Log.w(TAG, "x.n().e(0)");
        if (x.negate().eval(1) != -1.)
            Log.w(TAG, "x.n().e(1)");

        Log.d("Polynomial", "Test done");
    }
}
