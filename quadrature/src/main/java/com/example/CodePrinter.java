package com.example;

import java.io.IOException;
import java.io.PrintWriter;

public class CodePrinter {

    private static final String filename =
            "app/src/main/java/com/transvect/orbitalexplorer/QuadratureTables.java";
    private PrintWriter writer;
    private int N = 0;
    private int L = -1;

    private void line(String s) {
        writer.println(s);
    }

    private void line(int spaces, String s) {
        for (int i = 0; i < spaces; ++i)
            writer.print(' ');
        line(s);
    }

    private String myToStr(double d) {
        String r = Double.toString(d);
        while (r.length() < 19)
            r += " ";
        return r;
    }

    public void printPreface() throws IOException {
        writer = new PrintWriter(filename, "UTF-8");
        line("package com.transvect.orbitalexplorer;");
        line("");
        line("public final class QuadratureTables {");
        line("");
        line(4, "private QuadratureTables() {}");
        line("");
        line(4, "public final double[][][] quadratureTables = {");
    }

    public void print(int newN, int newL, double data[]) {
        if (newN != N) {
            if (N != 0) {
                // Close previous
                line(12, "},");
            }
            // Open new
            line(12, "{");
            if (newN != N + 1)
                System.err.println("Severe error: N out of order");
            N = newN;
            L = -1;
        }

        if (newL != L + 1)
            System.err.println("Severe error: L out of order");
        L = newL;
        line(20, "{");
        line(28, "// N = " + N + ", L = " + L);
        for (int i = 0; i < data.length; i += 2) {
            line(28, myToStr(data[i]) + ", " + myToStr(data[i + 1]) + ",");
        }
        line(20, "},");
    }

    public void printSuffix() {
        // close last
        line(12, "}");
        line(4, "};");
        line("}");
        writer.close();
    }
}
