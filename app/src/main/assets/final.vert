#version 300 es
// Input vertex position
in vec2 inPosition;

// Output texture coordinate
out vec2 texCoord;

void main() {
    gl_Position = vec4(inPosition.xy, 0, 1);
    texCoord = (inPosition.xy + 1.0) / 2.0;
}
