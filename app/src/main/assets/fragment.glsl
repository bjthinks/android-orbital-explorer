precision mediump float;
varying vec4 position;
void main() {
    gl_FragColor = vec4((position.xy + 1.0) / 2.0, (2.0 - position.x - position.y) / 4.0, 1);
}
