#version 300 es
in vec3 inPosition;
out vec3 color;
uniform mat4 projectionMatrix;
uniform mat4 scalingMatrix;
uniform float arrowSize;

void main() {
    gl_Position = projectionMatrix * scalingMatrix * vec4(inPosition, 1);
    gl_PointSize = arrowSize;
    color = inPosition; // hacky
}
