#version 300 es
precision highp int;
precision highp float;
uniform mat4 shaderTransform;
in vec3 front, back;
out ivec3 color;

float f(vec3 x) {
    vec3 foo = x * vec3(1.0, sqrt(2.0), 2.0);
    float nx2 = dot(foo, foo);
    return exp(-nx2);
}

void main() {
    float lum = 0.0;
    vec3 loc = back;
    const int steps = 10;
    vec3 inc = (front - back) / float(steps);
    lum += f(loc) / 2.0;
    for (int i = 1; i < steps; ++i) {
        loc += inc;
        lum += f(loc);
    }
    loc += inc;
    lum += f(loc) / 2.0;
    lum *= length(inc);
    lum /= 1.77245385091;
    vec2 white = vec2(0.19784, 0.46832);
    vec3 result = vec3(white, lum);
    color = ivec3(result * 2147483647.0);
}
