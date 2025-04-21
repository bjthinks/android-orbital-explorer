#version 300 es
in vec3 inPosition;
in vec3 inColor;
out vec3 color;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * vec4(inPosition.xyz, 1);
    color = inColor;
}
