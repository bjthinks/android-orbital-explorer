#version 300 es
precision highp int;
precision highp float;

in vec3 front, back;
out ivec3 color;

// Orbital
uniform sampler2D radial;

float f(vec3 x) {
    float r = length(x * vec3(1.0, sqrt(2.0), 2.0));
    float val;
    if (r > 1.0)
        val = 0.0;
    else
        val = texture(radial, vec2(r, 0.5)).x;
    return val;
}

void main() {
    float lum = 0.0;
    vec3 loc = back;
    const int steps = 4;
    vec3 inc = (front - back) / float(steps);
    lum += f(loc) / 2.0;
    for (int i = 1; i < steps; ++i) {
        loc += inc;
        lum += f(loc);
    }
    loc += inc;
    lum += f(loc) / 2.0;
    lum *= length(inc);
    vec2 white = vec2(0.19784, 0.46832);
    vec3 result = vec3(white, lum);
    color = ivec3(result * 2147483647.0);
}
