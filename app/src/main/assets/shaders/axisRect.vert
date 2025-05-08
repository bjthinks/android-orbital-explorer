#version 300 es
in vec3 inPosition;
//uniform mat4 projectionMatrix;
//uniform mat4 scalingMatrix;

void main() {
    gl_Position = vec4(inPosition.xyz, 1);
}
