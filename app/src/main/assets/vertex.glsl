// Matrix to be applied to coordinates
uniform mat4 shaderTransform;

// Input vertex position
attribute vec2 inPosition;

// Output pre-transformed position
varying vec4 position;

void main() {
    position = vec4(inPosition.xy, 0, 1);
    gl_Position = shaderTransform * position;
}
