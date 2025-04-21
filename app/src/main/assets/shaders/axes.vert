#version 300 es
// Input vertex position
in vec3 inPosition;

void main() {
    gl_Position = vec4(inPosition.xyz, 1);
}
