#version 300 es
precision highp int;
precision highp float;
uniform mat4 shaderTransform;
in vec4 front, back;
out ivec3 color;

float f(vec3 x) {
    float nx = length(x);
    return exp(-nx*nx);
}

void main() {
    vec4 original_front = inverse(shaderTransform) * front;
    vec4 original_back  = inverse(shaderTransform) * back;
    original_front /= original_front.w;
    original_back /= original_back.w;
    float lum = 0.0;
    vec3 loc = original_back.xyz;
    const int steps = 10;
    vec3 inc = (original_front - original_back).xyz / float(steps);
    lum += f(loc) / 2.0;
    for (int i = 1; i < steps - 1; ++i) {
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
