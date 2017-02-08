package com.gputreats.orbitalexplorer;

import java.io.IOException;

enum MaximumRadiusGenerator {
    ;

    private static double computeRadius(int qN, int qL) {
        RadialFunction rf = new RadialFunction(1, qN, qL);
        Function f = new Product(new Product(rf, rf), new Power(1));
        final double stepSize = 0.125;
        final double maxRadius = 1000.0;
        double total = f.eval(0.0) * (stepSize / 2.0);
        for (double r = stepSize; r < maxRadius; r += stepSize)
            total += f.eval(r) * stepSize;
        double m = maxRadius;
        double mtot = 0.0;
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
        for (int qN = 0; qN <= BaseOrbital.MAX_N; ++qN) {
            for (int qL = 0; qL < qN; ++qL)
                codePrinter.printNumber(computeRadius(qN, qL));
            if (qN < BaseOrbital.MAX_N)
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
