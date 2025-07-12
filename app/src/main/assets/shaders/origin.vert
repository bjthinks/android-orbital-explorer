#version 300 es
in vec3 inPosition;
uniform float originSize;

void main() {
    gl_Position = vec4(inPosition, 1);
    gl_PointSize = originSize;
}
