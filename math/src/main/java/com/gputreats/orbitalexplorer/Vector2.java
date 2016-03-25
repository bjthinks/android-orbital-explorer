package com.gputreats.orbitalexplorer;

public class Vector2 {

    private final double x, y;

    public double getX() { return x; }
    public double getY() { return y; }

    public Vector2(double xx, double yy) {
        x = xx;
        y = yy;
    }

    // Primitive operations

    public Vector2 add(Vector2 rhs) {
        return new Vector2(x + rhs.x, y + rhs.y);
    }

    public Vector2 multiply(double c) {
        return new Vector2(x * c, y * c);
    }

    public double dot(Vector2 rhs) {
        return x * rhs.x + y * rhs.y;
    }

    // Composite operations

    public Vector2 negate() {
        return multiply(-1.0);
    }

    public Vector2 subtract(Vector2 rhs) {
        return add(rhs.negate());
    }

    public Vector2 divide(double c) {
        return multiply(1.0 / c);
    }

    public double normSquared() {
        return dot(this);
    }

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public Vector2 normalize() {
        return divide(norm());
    }
}
