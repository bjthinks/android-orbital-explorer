#version 300 es
precision mediump float;
out vec3 outColor;
uniform sampler2D origin;

void main() {
    vec2 pc = gl_PointCoord; // Needs to be in a local variable for ZTE N817
    float t = texture(origin, pc).r;
    outColor = vec3(t, t, t);
}
