package com.gputreats.orbitalexplorer;

class Spew {
    private long a, b;

    Spew(long a_, long b_) {
        a = a_;
        b = b_;
        if (a == 0L && b == 0L)
            throw new RuntimeException();
        for (int i = 0; i < 37; ++i)
            get();
    }

    public final int get() {
        long x = a;
        long y = b;
        a = y;
        x ^= x << 23;
        b = x ^ y ^ (x >>> 17) ^ (y >>> 26);
        return (int) (b + y);
    }
}
