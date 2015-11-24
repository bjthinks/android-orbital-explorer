package com.transvect.orbitalexplorer;

import android.util.Log;

public class Polynomial implements Function {
    private static final String TAG = "Polynomial";

    // Invariant: c is not null, and leading coeff is not 0.
    // The zero polynomial has c.length == 0 and degree -1.
    private double c[];

    public Polynomial() {
        c = new double[0];
    }

    public Polynomial(double cc) {
        if (cc == 0.)
            c = new double[0];
        else {
            c = new double[1];
            c[0] = cc;
        }
    }

    public int degree() {
        return c.length - 1;
    }

    public static Polynomial variableToThe(int power) {
        Polynomial x = new Polynomial();
        x.c = new double[power + 1];
        x.c[power] = 1.;
        return x;
    }

    public static Polynomial variable() {
        return variableToThe(1);
    }

    public double eval(double x) {
        double result = 0.;
        for (int i = c.length - 1; i >= 0; --i) {
            result *= x;
            result += c[i];
        }
        return result;
    }

    public Polynomial add(Polynomial rhs) {
        Polynomial result = new Polynomial();

        int needToDoAdditionUpTo;
        if (degree() < rhs.degree()) {
            result.c = new double[rhs.c.length];
            System.arraycopy(rhs.c, c.length, result.c, c.length, rhs.c.length - c.length);
            needToDoAdditionUpTo = c.length;
        } else if (degree() > rhs.degree()) {
            result.c = new double[c.length];
            System.arraycopy(c, rhs.c.length, result.c, rhs.c.length, c.length - rhs.c.length);
            needToDoAdditionUpTo = rhs.c.length;
        } else {
            int len = c.length;
            while (len > 0 && c[len - 1] + rhs.c[len - 1] == 0)
                --len;
            result.c = new double[len];
            needToDoAdditionUpTo = len;
        }
        for (int i = 0; i < needToDoAdditionUpTo; ++i)
            result.c[i] = c[i] + rhs.c[i];
        return result;
    }

    public Polynomial negate() {
        Polynomial result = new Polynomial();
        result.c = new double[c.length];
        for (int i = 0; i < c.length; ++i) {
            result.c[i] = -c[i];
        }
        return result;
    }

    public Polynomial subtract(Polynomial rhs) {
        return add(rhs.negate());
    }

    public Polynomial multiply(Polynomial rhs) {
        Polynomial result = new Polynomial();
        if (c.length == 0 || rhs.c.length == 0)
            return result;
        result.c = new double[c.length + rhs.c.length - 1];
        for (int i = 0; i < c.length; ++i)
            for (int j = 0; j < rhs.c.length; ++j)
                result.c[i + j] += c[i] * rhs.c[j];
        if (result.c[result.c.length - 1] == 0.)
            Log.w(TAG, "Polynomial multiplication underflow");
        return result;
    }

    public Polynomial pow(int power) {
        Polynomial result = new Polynomial(1.0);
        for (int i = 0; i < power; ++i)
            result = result.multiply(this);
        return result;
    }

    public Polynomial add(double c) {
        return add(new Polynomial(c));
    }

    public Polynomial subtract(double c) {
        return add(-c);
    }

    public Polynomial multiply(double c) {
        return multiply(new Polynomial(c));
    }

    public Polynomial derivative() {
        Polynomial result = new Polynomial();

        if (c.length <= 1)
            return result;

        result.c = new double[c.length - 1];
        for (int i = 1; i < c.length; ++i)
            result.c[i - 1] = c[i] * (float) i;

        return result;
    }

    public double coefficient(int i) {
        return c[i];
    }

    /* @Override
    public String toString() {
        String result = "";
        for (int d = c.length - 1; d >= 0; --d)
            if (c[d] != 0) {
                if (result != "")
                    result += " + ";
                result += c[d];
                if (d >= 2)
                    result += " x^" + d;
                else if (d == 1)
                    result += " x";
            }
        return result;
    }

    public static void test() {
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

        testSame("-0", zero.negate(), zero);
        testSame("-1", one.negate(), new Polynomial(-1));
        if (x.negate().eval(0) != 0.)
            Log.w(TAG, "x.n().e(0)");
        if (x.negate().eval(1) != -1.)
            Log.w(TAG, "x.n().e(1)");

        testSame("0+0", zero.add(zero), zero);
        testSame("0+1", zero.add(one), one);
        testSame("1+0", one.add(zero), one);
        testSame("1+1", one.add(one), new Polynomial(2));
        testSame("0+x", zero.add(x), x);
        testSame("x+0", x.add(zero), x);
        Polynomial xplusone = Polynomial.variable();
        xplusone.c[0] = 1;
        testSame("1+x", one.add(x), xplusone);
        testSame("x+1", x.add(one), xplusone);
        Polynomial twox = Polynomial.variable();
        twox.c[1] = 2;
        testSame("x+x", x.add(x), twox);

        Polynomial negone = one.negate();
        testSame("1+-1", one.add(negone), zero);
        Polynomial negx = x.negate();
        testSame("x+-x", x.add(negx), zero);
        testSame("(1+x)+-x=1", one.add(x).add(negx), one);
        testSame("(1+x)+-1=x", one.add(x).add(negone), x);

        testSame("1-x", one.subtract(x), one.add(negx));
        testSame("x-1", x.subtract(one), x.add(negone));

        testSame("0*0", zero.multiply(zero), zero);
        testSame("0*1", zero.multiply(one), zero);
        testSame("1*0", one.multiply(zero), zero);
        testSame("1*1", one.multiply(one), one);
        testSame("x*0", x.multiply(zero), zero);
        testSame("x*1", x.multiply(one), x);
        testSame("0*x", zero.multiply(x), zero);
        testSame("1*x", one.multiply(x), x);
        Polynomial x2 = new Polynomial();
        x2.c = new double[3];
        x2.c[0] = 0;
        x2.c[1] = 0;
        x2.c[2] = 1;
        testSame("x*x", x.multiply(x), x2);

        // (x-2) * (x+3) = x^2 + x - 6
        testSame("(x-2)(x+3)", x.subtract(new Polynomial(2)).multiply(x.add(new Polynomial(3))),
                x2.add(x).subtract(new Polynomial(6)));
        testSame("(x-2)(x+3)", x.subtract(2).multiply(x.add(3)),
                x2.add(x).subtract(6));

        // x+x = 2x
        testSame("x+x", x.add(x), x.multiply(2));

        testSame("0'=0", zero.derivative(), zero);
        testSame("1'=0", one.derivative(), zero);
        testSame("x'=1", x.derivative(), one);
        testSame("x^2'=2x", x2.derivative(), x.multiply(2));

        // f = 3 x^3 + 4 x^2 + 6 x + 8
        Polynomial f = x2.multiply(x).multiply(3).add(x2.multiply(4))
                .add(x.multiply(6)).add(one.multiply(8));
        // df = 9 x^2 + 8 x + 6
        Polynomial df = x2.multiply(9).add(x.multiply(8)).add(one.multiply(6));
        testSame("f'=df", f.derivative(), df);

        testSame("x*x=x^2", x2, variableToThe(2));
        testSame("x*x=(x)^2", x2, x.pow(2));

        if (x2.coefficient(0) != 0.0 || x2.coefficient(1) != 0.0 || x2.coefficient(2) != 1.0)
            Log.w(TAG, "coefficient");

        Log.d("Polynomial", "Test done");
    }

    private static void testSame(String tag, Polynomial f, Polynomial g) {
        tag = "testSame " + tag;
        if (f.degree() != g.degree())
            Log.w(tag, "degree");
        if (f.eval(0.) != g.eval(0.))
            Log.w(tag, "eval(0)");
        if (f.eval(1.) != g.eval(1.))
            Log.w(tag, "eval(1)");
        if (f.eval(-5.) != g.eval(-5.))
            Log.w(tag, "eval(-5)");
    } */
}
