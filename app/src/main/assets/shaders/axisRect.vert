#version 300 es
in vec3 inPosition;
in float inSide;
in vec3 inColor;
out vec3 color;
out vec2 texCoord;
uniform mat4 axisMatrix;
uniform mat4 projectionMatrix;
uniform mat4 scalingMatrix;

void main() {
    vec4 linePosition = projectionMatrix * scalingMatrix * vec4(inPosition, 1);
    vec4 widthPosition = axisMatrix * vec4(0, inSide, 0, 0);
    gl_Position = vec4(linePosition + widthPosition * linePosition.w);
    texCoord = vec2((inSide + 1.0) / 2.0, 0.5);
    color = inColor;
}
