#version 300 es
in vec3 inPosition;
out vec2 texCoord;
uniform mat4 axisMatrix;
uniform mat4 projectionMatrix;
uniform mat4 scalingMatrix;

void main() {
    vec4 linePosition = projectionMatrix * scalingMatrix * vec4(inPosition.x, 0, 0, 1);
    vec4 widthPosition = axisMatrix * vec4(0, inPosition.y, 0, 0);
    gl_Position = vec4(linePosition / linePosition.w + widthPosition) * linePosition.w;
    texCoord = vec2((inPosition.y + 1.0) / 2.0, 0.5);
}
