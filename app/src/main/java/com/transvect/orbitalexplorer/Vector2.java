package com.transvect.orbitalexplorer;

public class Vector2 {

    private final double x, y;

    public double getX() { return x; }
    public double getY() { return y; }

    public Vector2(double xx, double yy) {
        x = xx;
        y = yy;
    }

    public Vector2 add(Vector2 rhs) {
        return new Vector2(x + rhs.x, y + rhs.y);
    }

    public Vector2 subtract(Vector2 rhs) {
        return new Vector2(x - rhs.x, y - rhs.y);
    }

    public Vector2 multiply(double c) {
        return new Vector2(x * c, y * c);
    }

    public Vector2 divide(double c) {
        return new Vector2(x / c, y / c);
    }

    /* Vector2 negate() {
        return new Vector2(-x, -y);
    } */

    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        return divide(norm());
    }
}
