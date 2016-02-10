package com.example;

import java.io.IOException;

public class QuadratureGenerator {

    private CodePrinter codePrinter;

    private void go() throws IOException {
        codePrinter = new CodePrinter();
        codePrinter.printPreface();
        double data[] =
                {
                        0.5857864376269006, 0.8535533905932715,
                        3.414213562373088, 0.14644660940672669
                };
        codePrinter.print(1, 0, data);
        codePrinter.print(2, 0, data);
        codePrinter.print(2, 1, data);
        codePrinter.printSuffix();
    }

    public static void main(String args[]) {
        QuadratureGenerator q = new QuadratureGenerator();
        try {
            q.go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
        }
    }
}
