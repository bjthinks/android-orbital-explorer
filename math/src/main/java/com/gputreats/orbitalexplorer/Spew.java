package com.gputreats.orbitalexplorer;

public class Spew {
    long a, b;

    public Spew(long a_, long b_) {
        a = a_;
        b = b_;
        if (a == 0 && b == 0)
            throw new RuntimeException();
        for (int i = 0; i < 37; ++i)
            get();
    }

    public int get() {
        long x = a;
        long y = b;
        a = y;
        x ^= x << 23;
        b = x ^ y ^ (x >>> 17) ^ (y >>> 26);
        return (int) (b + y);
    }
}
