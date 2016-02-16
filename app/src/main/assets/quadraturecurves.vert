#version 300 es
// Input vertex position
in vec2 inPosition;

void main() {
    gl_Position = vec4(inPosition.xy, 0, 1);
}
