#version 300 es
in vec3 inPosition;
out vec3 color;
flat out int discardMe;
out mat2 rotation;
uniform mat4 projectionMatrix;
uniform mat4 scalingMatrix;
uniform float arrowSize;
uniform vec2 screenDimensions;

void main() {
    gl_Position = projectionMatrix * scalingMatrix * vec4(inPosition, 1);
    gl_PointSize = arrowSize;
    color = inPosition; // hacky
    vec2 r = gl_Position.xy * screenDimensions;
    if (length(r) == 0.0)
        discardMe = 1;
    else
        discardMe = 0;
    r /= length(r);
    rotation = mat2(r.x, r.y, -r.y, r.x);
}
