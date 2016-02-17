#version 300 es
// Input vertex position
in vec2 position;

uniform vec2 scale;

void main() {
    gl_Position = vec4(scale * position.xy, 0, 1);
}
