package com.transvect.orbitalexplorer;

public class Vector2 {

    private final double x, y;

    public double getX() { return x; }
    public double getY() { return y; }

    Vector2(double xx, double yy) {
        x = xx;
        y = yy;
    }

    Vector2 add(Vector2 rhs) {
        return new Vector2(x + rhs.x, y + rhs.y);
    }

    Vector2 subtract(Vector2 rhs) {
        return new Vector2(x - rhs.x, y - rhs.y);
    }

    Vector2 multiply(double c) {
        return new Vector2(x * c, y * c);
    }

    Vector2 divide(double c) {
        return new Vector2(x / c, y / c);
    }

    /* Vector2 negate() {
        return new Vector2(-x, -y);
    } */

    double norm() {
        return Math.sqrt(x * x + y * y);
    }

    Vector2 normalize() {
        return divide(norm());
    }
}
