package com.gputreats.orbitalexplorer;

import java.io.IOException;
import java.io.PrintWriter;

class CodePrinter {

    private PrintWriter writer;

    CodePrinter(String filename) throws IOException {
        writer = new PrintWriter(filename, "UTF-8");
    }

    private void line(String s) {
        writer.println(s);
    }

    void printPreface() {
        line("package com.gputreats.orbitalexplorer;");
        line("");
        line("public final class MaximumRadiusTable {");
        line("");
        line("    private MaximumRadiusTable() {}");
        line("");
        line("    private static final double maximumRadiusTable[][] = {");
        line("            {");
    }

    void printNumber(double x) {
        line("                    " + x + ",");
    }

    void printSeparator() {
        line("            },");
        line("            {");
    }

    void printSuffix() {
        line("            }");
        line("    };");
        line("");
        line("    public static double getMaximumRadius(int N, int L) {");
        line("        return maximumRadiusTable[N][L];");
        line("    }");
        line("}");
        writer.close();
    }
}
