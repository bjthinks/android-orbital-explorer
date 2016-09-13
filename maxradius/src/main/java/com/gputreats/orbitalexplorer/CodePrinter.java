package com.gputreats.orbitalexplorer;

import java.io.IOException;
import java.io.PrintWriter;

class CodePrinter {

    private final PrintWriter writer;

    CodePrinter(String filename) throws IOException {
        writer = new PrintWriter(filename, "UTF-8");
    }

    private void line(String s) {
        writer.println(s);
    }

    void printPreface() {
        line("package com.gputreats.orbitalexplorer;");
        line("");
        line("final class MaximumRadiusTable {");
        line("");
        line("    private MaximumRadiusTable() {}");
        line("");
        line("    private static final double[][] MAXIMUM_RADIUS_TABLE = {");
        line("            {");
    }

    void printNumber(double x) {
        line("                    " + x + ',');
    }

    void printSeparator() {
        line("            },");
        line("            {");
    }

    void printSuffix() {
        line("            }");
        line("    };");
        line("");
        line("    static double getMaximumRadius(int qN, int qL) {");
        line("        return MAXIMUM_RADIUS_TABLE[qN][qL];");
        line("    }");
        line("}");
        writer.close();
    }
}
