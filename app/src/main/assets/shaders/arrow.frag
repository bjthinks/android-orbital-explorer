#version 300 es
precision mediump float;
in vec3 color;
out vec3 outColor;

void main() {
    vec2 position = 2.0 * gl_PointCoord - vec2(1, 1);
    outColor = color;
}
