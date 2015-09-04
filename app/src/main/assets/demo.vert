#version 300 es
// Matrix to be applied to coordinates
uniform mat4 shaderTransform;

// Input vertex position
in vec2 inPosition;

// Output pre-transformed position
out vec2 position;

void main() {
    position = inPosition * 0.06;
    gl_Position = shaderTransform * vec4(inPosition, 0, 1);
}
