#version 300 es
precision mediump float;
out vec3 outColor;

void main() {
    vec2 position = 2.0 * gl_PointCoord - vec2(1, 1);
    if (length(position) > 1.0)
        discard;
    outColor = vec3(1, 1, 1);
}
