#version 300 es
precision mediump float;
in vec3 color;
out vec3 outColor;
uniform sampler2D arrow;

void main() {
    float t = texture(arrow, gl_PointCoord).r;
    outColor = vec3(color * t);
}
