#version 300 es
in vec3 inPosition;
out vec2 texCoord;
uniform mat4 axisMatrix;
//uniform mat4 projectionMatrix;
//uniform mat4 scalingMatrix;

void main() {
    gl_Position = axisMatrix * vec4(inPosition.xyz, 1);
    texCoord = vec2((inPosition.y + 1.0) / 2.0, 0.5);
}
