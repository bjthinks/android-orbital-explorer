#version 300 es

// Matrix to be applied to coordinates
uniform mat4 inverseTransform;

// Input vertex position = (+/-1, +/-1)
in vec2 inPosition;

// Output near and far coords, where the line of sight intersects the frustum
out vec3 near, far;

void main() {
    gl_Position = vec4(inPosition, 0, 1);
    vec4 preNear = inverseTransform * vec4(inPosition, -1, 1);
    vec4 preFar = inverseTransform * vec4(inPosition, 1, 1);
    near = preNear.xyz / preNear.w;
    far = preFar.xyz / preFar.w;
}
