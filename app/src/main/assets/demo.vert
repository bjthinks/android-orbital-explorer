#version 300 es
// Matrix to be applied to coordinates
uniform mat4 shaderTransform;

// Input vertex position
in vec2 inPosition;

// Output pre-transformed position
out vec4 position;

void main() {
    position = vec4(inPosition.xy, 0, 1);
    gl_Position = shaderTransform * position;
}
