uniform mat4 uMVPMatrix;
attribute vec2 inPosition;
varying vec4 position;
void main() {
    position = vec4(inPosition.xy, 0, 1);
    gl_Position = uMVPMatrix * position;
}
