#version 300 es
precision mediump float;
uniform sampler2D data;
in vec2 texCoord;
out vec3 color;
void main() {
    vec4 blet = texture(data, texCoord);
    color = blet.xyz;
}
