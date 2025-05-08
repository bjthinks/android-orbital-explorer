#version 300 es
precision mediump float;
in vec2 texCoord;
out vec3 outColor;
uniform vec3 color;
uniform sampler2D axis;

void main() {
    float t = texture(axis, texCoord).r;
    outColor = color * t;
}
