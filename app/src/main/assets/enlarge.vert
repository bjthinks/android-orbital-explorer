#version 300 es
// Input vertex position
in vec2 inPosition;

// Output texture coordinate
out vec2 boxCoord;

void main() {
    gl_Position = vec4(inPosition.xy, 0, 1);
    boxCoord = (inPosition.xy + 1.0) / 2.0;
}
