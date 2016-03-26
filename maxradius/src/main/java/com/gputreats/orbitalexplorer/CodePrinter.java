package com.gputreats.orbitalexplorer;

import java.io.IOException;
import java.io.PrintWriter;

public class CodePrinter {

    private PrintWriter writer;

    public CodePrinter(String filename) throws IOException {
        writer = new PrintWriter(filename, "UTF-8");
    }

    private void line(String s) {
        writer.println(s);
    }

    public void printPreface() {
        line("package com.gputreats.orbitalexplorer;");
        line("");
        line("public class MaximumRadiusTable {");
        line("");
        line("    private static double maximumRadiusTable[][] = {");
        line("            {");
    }

    public void printNumber(double x) {
        line("                    " + x + ",");
    }

    public void printSeparator() {
        line("            },");
        line("            {");
    }

    public void printSuffix() {
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
