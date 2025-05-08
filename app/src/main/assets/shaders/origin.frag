#version 300 es
precision mediump float;
out vec3 outColor;
uniform sampler2D origin;

void main() {
    float t = texture(origin, gl_PointCoord).r;
    outColor = vec3(1, 1, 1) * t;
}
