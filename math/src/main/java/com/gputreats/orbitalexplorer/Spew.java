package com.gputreats.orbitalexplorer;

class Spew {
    private long a, b;

    Spew(int a_, int b_) {
        a = (long) a_;
        b = (long) b_;
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
        b = x ^ y ^ x >>> 17 ^ y >>> 26;
        return (int) (b + y);
    }
}
