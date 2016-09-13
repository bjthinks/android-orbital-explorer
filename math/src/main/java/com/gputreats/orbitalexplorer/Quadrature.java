package com.gputreats.orbitalexplorer;

class Quadrature {

    public final int N, L;
    public final boolean color;
    private final int order;
    private final int steps;

    Quadrature(int inN, int inL, boolean inColor) {
        N = inN;
        L = inL;
        color = inColor;

        // An important design choice is to make the quadrature order independent of M. This
        // lets us re-use quadrature nodes and weights for all orbitals with the same
        // (Z, N, L). Combined with the design choice to set Z = N always, this means we
        // only need nodes and weights for each of the (N, L) pairs.
        // Experiments show that a very simple formula here suffices:
        // N     = pretty good, slight defects near center at some viewing angles
        // N + 1 = extremely good, defects present but not
        //         visible without direct comparison to N + 2
        // N + 2 = essentially perfect, visually identical to all higher orders for all but
        //         a few very specific corner cases
        order = color ? N + 1 : L + 2;

        steps = color ? 64 : 1024;
    }

    int getOrder() {
        return order;
    }

    int getSteps() {
        return steps;
    }
}
