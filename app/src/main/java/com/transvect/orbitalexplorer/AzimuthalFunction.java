package com.transvect.orbitalexplorer;

/**
 * Azimuthal part of the wave function of a one-electron atom.
 * Theta(theta) depends on constants L and M and is a polynomial in sin & cos of theta
 *
 * Theta(theta) = sin(theta)
 */

public class AzimuthalFunction {

    public AzimuthalFunction() {}

    // Note that theta is "colatitude", i.e. 0 along (0, 0, 1), pi/2 when z=0,
    // and pi along (0, 0, -1).
    public double eval(double theta) {
        return Math.sin(theta);
    }
}
