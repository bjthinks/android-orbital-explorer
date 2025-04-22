#version 300 es
precision mediump float;
in vec3 color;
flat in int discardMe;
in mat2 rotation;
out vec3 outColor;
uniform sampler2D arrow;

void main() {
    if (discardMe == 1)
        discard;
    vec2 c = gl_PointCoord;
    c = 2.0 * c - vec2(1, 1);
    c = rotation * c;
    c = 0.5 * (c + vec2(1, 1));
    float t = texture(arrow, c).r;
    outColor = vec3(color * t);
}
