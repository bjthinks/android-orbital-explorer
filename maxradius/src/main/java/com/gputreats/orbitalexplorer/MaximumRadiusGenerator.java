package com.gputreats.orbitalexplorer;

import java.io.IOException;

final class MaximumRadiusGenerator {

    private MaximumRadiusGenerator() {}

    private static double computeRadius(int N, int L) {
        RadialFunction rf = new RadialFunction(1, N, L);
        Function f = new Product(new Product(rf, rf), new Power(1));
        double stepSize = 0.125;
        double maxRadius = 1000.0;
        double total = f.eval(0.0) * (stepSize / 2.0);
        for (double r = stepSize; r <= maxRadius; r += stepSize)
            total += f.eval(r) * stepSize;
        double m = maxRadius;
        double mtot = f.eval(m) * stepSize;
        while (Math.abs(mtot) < 1.0e-4 * Math.abs(total)) {
            m -= stepSize;
            mtot += f.eval(m) * stepSize;
        }
        return m;
    }

    private static void go() throws IOException {
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

    public static void main(String[] args) {
        try {
            go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
