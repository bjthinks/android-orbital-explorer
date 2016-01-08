#version 300 es
precision highp float;

out vec2 color;

void main() {
    vec2 total = vec2(0);
    for (int i = 0; i < 1; ++i) {
        float phi = atan(0.0, 1.0);
        total += vec2(1.0, 0.0);
    }
    color = total * 2147483647.0;
}
