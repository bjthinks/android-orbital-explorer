#version 300 es
uniform float originSize;

void main() {
    gl_Position = vec4(0, 0, 0, 1);
    gl_PointSize = originSize;
}
