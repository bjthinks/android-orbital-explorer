package com.transvect.orbitalexplorer;

/**
 * Azimuthal part of the wave function of a one-electron atom.
 * Theta(theta) depends on constants L and M and is a polynomial in sin & cos of theta
 *
 * Theta(theta) = sin(theta)
 */

public class AzimuthalFunction {

    public AzimuthalFunction() {}

    public double eval(double theta) {
        return Math.sin(theta);
    }
}
