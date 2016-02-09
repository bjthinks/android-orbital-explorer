package com.example;

import java.io.IOException;
import java.io.PrintWriter;

public class CodePrinter {

    private static final String filename =
            "app/src/main/java/com/transvect/orbitalexplorer/QuadratureTables.java";
    private PrintWriter writer;

    private void line(String s) {
        writer.println(s);
    }

    public void printPreface() throws IOException {
        writer = new PrintWriter(filename, "UTF-8");
        line("package com.transvect.orbitalexplorer;");
        line("");
        line("public class QuadratureTables {");
    }

    public void printSuffix() {
        line("}");
        writer.close();
    }
}
