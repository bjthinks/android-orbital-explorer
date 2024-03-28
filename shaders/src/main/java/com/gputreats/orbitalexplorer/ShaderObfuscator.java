package com.gputreats.orbitalexplorer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

enum ShaderObfuscator {
    ;

    private static void obfuscate(String infile, String outfile) throws IOException {
        final int buildVersion = 131;
        String infilename = "shaders/src/main/java/" + infile;
        String outfilename = "app/src/main/assets/a/" + outfile;
        BufferedInputStream instream =
                new BufferedInputStream(new FileInputStream(infilename));
        BufferedOutputStream outstream =
                new BufferedOutputStream(new FileOutputStream(outfilename));
        int b = instream.read();
        int c = (int) outfile.charAt(0) + 100 * buildVersion;
        Spew spew = new Spew(c, c);
        while (b != -1) {
            outstream.write(b ^ spew.get());
            b = instream.read();
        }
        instream.close();
        outstream.close();
    }

    public static void main(String[] args) {
        try {
            obfuscate("integrator_color.frag",   "1");
            obfuscate("integrator_color.vert",   "2");
            obfuscate("integrator_mono.frag",    "3");
            obfuscate("integrator_mono.vert",    "4");
            obfuscate("screendrawer_color.frag", "5");
            obfuscate("screendrawer_color.vert", "6");
            obfuscate("screendrawer_mono.frag",  "7");
            obfuscate("screendrawer_mono.vert",  "8");
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
