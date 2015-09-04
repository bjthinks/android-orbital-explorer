#version 300 es
precision mediump float;
in vec2 position;
out vec3 color;
void main() {
    if (length(position) > 0.06)
        discard;
    vec2 white = vec2(0.19784, 0.46832);
    color = vec3(white + position, 0.5);
}
