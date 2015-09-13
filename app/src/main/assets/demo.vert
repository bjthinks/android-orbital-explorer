#version 300 es

// Matrix to be applied to coordinates
uniform mat4 shaderTransform;

// Input vertex position = (+/-1, +/-1)
in vec2 inPosition;

// Output front and back coords
out vec3 front, back;

void main() {
    gl_Position = vec4(inPosition, 0, 1);
    vec4 prefront = inverse(shaderTransform) * vec4(inPosition, 1, 1);
    vec4 preback = inverse(shaderTransform) * vec4(inPosition, -1, 1);
    front = prefront.xyz / prefront.w;
    back = preback.xyz / preback.w;
}
