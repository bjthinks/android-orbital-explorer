package com.example;

import java.io.IOException;

public class QuadratureGenerator {

    CodePrinter codePrinter;

    public void go() throws IOException {
        codePrinter = new CodePrinter();
        codePrinter.printPreface();
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
