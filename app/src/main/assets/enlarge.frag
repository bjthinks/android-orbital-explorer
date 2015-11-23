#version 300 es
precision highp int;
precision highp float;
uniform sampler2D data;
in vec2 texCoord;
out vec3 color;

void main() {
    color = vec3(texture(data, texCoord).xyz);
}
