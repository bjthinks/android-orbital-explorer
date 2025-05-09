#version 300 es
precision mediump float;
in vec2 texCoord;
in vec3 color;
out vec3 outColor;
uniform sampler2D axis;

void main() {
    float t = texture(axis, texCoord).r;
    outColor = color * t;
}
