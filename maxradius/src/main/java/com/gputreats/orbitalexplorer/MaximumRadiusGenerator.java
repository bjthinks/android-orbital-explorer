package com.gputreats.orbitalexplorer;

import java.io.IOException;

public class MaximumRadiusGenerator {

    private double computeRadius(int N, int L) {
        RadialFunction rf = new RadialFunction(1, N, L);
        Function f = new Product(new Product(rf, rf), new Power(1));
        double total = f.eval(0.0) / 16.0;
        for (double r = 0.125; r <= 1000.0; r += 0.125)
            total += f.eval(r) / 8.0;
        double m = 1000.0;
        double mtot = f.eval(m) / 8.0;
        while (Math.abs(mtot) < 1e-4 * Math.abs(total)) {
            m -= 0.125;
            mtot += f.eval(m) / 8.0;
        }
        return m;
    }

    private void go() throws IOException {
        String filename =
                "math/src/main/java/com/gputreats/orbitalexplorer/MaximumRadiusTable.java";
        CodePrinter codePrinter = new CodePrinter(filename);
        codePrinter.printPreface();
        for (int N = 0; N <= 8; ++N) {
            for (int L = 0; L < N; ++L)
                codePrinter.printNumber(computeRadius(N, L));
            if (N < 8)
                codePrinter.printSeparator();
        }
        codePrinter.printSuffix();
    }

    public static void main(String args[]) {
        MaximumRadiusGenerator m = new MaximumRadiusGenerator();
        try {
            m.go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
