#version 300 es
precision mediump float;
in vec4 position;
out vec3 color;
void main() {
    color = vec3((position.xy + 1.0) / 2.0, 0.07);
}
