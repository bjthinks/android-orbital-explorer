#version 300 es
precision mediump float;
uniform sampler2D data;
in vec4 position;
out vec3 color;
void main() {
    vec2 texCoord = (position.xy + 1.0) / 2.0;
    vec4 blet = texture(data, texCoord);
    color = blet.xyz;
}
