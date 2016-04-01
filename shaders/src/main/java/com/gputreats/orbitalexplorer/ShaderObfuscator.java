package com.gputreats.orbitalexplorer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShaderObfuscator {

    private void obfuscate(String infile, String outfile, boolean dev) throws IOException {
        String infilename = "shaders/src/main/java/" + infile;
        String outfilename = "app/src/" + (dev ? "dev" : "main") + "/assets/a/" + outfile;
        BufferedInputStream instream =
                new BufferedInputStream(new FileInputStream(infilename));
        BufferedOutputStream outstream =
                new BufferedOutputStream(new FileOutputStream(outfilename));
        int b = instream.read();
        int c = outfile.charAt(0);
        Spew spew = new Spew(c, c);
        while (b != -1) {
            outstream.write(b ^ (spew.get() & 255));
            b = instream.read();
        }
        instream.close();
        outstream.close();
    }

    private void go() throws IOException {
        obfuscate("integrator_color.frag",   "1", false);
        obfuscate("integrator_color.vert",   "2", false);
        obfuscate("integrator_mono.frag",    "3", false);
        obfuscate("integrator_mono.vert",    "4", false);
        obfuscate("screendrawer_color.frag", "5", false);
        obfuscate("screendrawer_color.vert", "6", false);
        obfuscate("screendrawer_mono.frag",  "7", false);
        obfuscate("screendrawer_mono.vert",  "8", false);
        obfuscate("quadraturecurves.vert",   "a",  true);
        obfuscate("quadraturecurves.frag",   "b", true);
    }

    public static void main(String args[]) {
        ShaderObfuscator s = new ShaderObfuscator();
        try {
            s.go();
        } catch (Exception e) {
            System.err.print(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
