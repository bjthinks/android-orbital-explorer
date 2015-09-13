#version 300 es
precision highp int;
precision highp float;
in vec2 position;
out ivec3 color;
void main() {
    if (length(position) > 0.06)
        discard;
    vec2 white = vec2(0.19784, 0.46832);
    vec3 result = vec3(white + position, 0.5);
    color = ivec3(result * 2147483647.0);
}
