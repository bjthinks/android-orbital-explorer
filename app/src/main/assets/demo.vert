#version 300 es
// Matrix to be applied to coordinates
uniform mat4 shaderTransform;

// Input vertex position = (+/-1, +/-1)
in vec2 inPosition;

// Output front and back coords
out vec4 front, back;

void main() {
    gl_Position = vec4(inPosition,  0, 1);
    front       = vec4(inPosition,  1, 1);
    back        = vec4(inPosition, -1, 1);
}
