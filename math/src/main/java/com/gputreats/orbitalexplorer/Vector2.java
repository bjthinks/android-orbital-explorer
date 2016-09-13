package com.gputreats.orbitalexplorer;

class Vector2 {

    private final double x, y;

    double getX() { return x; }
    double getY() { return y; }

    Vector2(double inX, double inY) {
        x = inX;
        y = inY;
    }

    // Primitive operations

    Vector2 add(Vector2 rhs) {
        return new Vector2(x + rhs.x, y + rhs.y);
    }

    Vector2 multiply(double c) {
        return new Vector2(x * c, y * c);
    }

    double dot(Vector2 rhs) {
        return x * rhs.x + y * rhs.y;
    }

    // Composite operations

    Vector2 negate() {
        return multiply(-1.0);
    }

    Vector2 subtract(Vector2 rhs) {
        return add(rhs.negate());
    }

    Vector2 divide(double c) {
        return multiply(1.0 / c);
    }

    double normSquared() {
        return dot(this);
    }

    double norm() {
        return Math.sqrt(normSquared());
    }

    Vector2 normalize() {
        return divide(norm());
    }
}
