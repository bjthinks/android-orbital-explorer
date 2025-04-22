#version 300 es
in vec3 inPosition;
out vec3 color;
uniform float arrowSize;

void main() {
    gl_Position = vec4(inPosition, 1);
    gl_PointSize = arrowSize;
    color = inPosition; // hacky
}
