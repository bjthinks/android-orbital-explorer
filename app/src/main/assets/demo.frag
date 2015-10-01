#version 300 es
precision highp int;
precision highp float;

const vec2 white = vec2(0.19784, 0.46832);

in vec3 front, back;
out ivec3 color;

// Orbital
uniform sampler2D radial;
const float maximumRadius = 1.0;
const float numRadialSubdivisions = 10.0;

float radialPart(float r) {
    float positionInTexture = r / maximumRadius * numRadialSubdivisions;
    if (positionInTexture >= numRadialSubdivisions)
        return 0.0;
    float leftTexturePosition = trunc(positionInTexture);
    float leftTextureValue = texelFetch(radial, ivec2(leftTexturePosition, 0), 0).x;
    float rightTexturePosition = leftTexturePosition + 1.0;
    float rightTextureValue = texelFetch(radial, ivec2(rightTexturePosition, 0), 0).x;
    float interpolationValue = fract(positionInTexture);
    return mix(leftTextureValue, rightTextureValue, interpolationValue);
}

float f(vec3 x) {
    float r = length(x * vec3(1.0, sqrt(2.0), 2.0));
    return radialPart(r);
}

void main() {
    float distanceFromOrigin = length(cross(front, back)) / length(front - back);
    if (distanceFromOrigin > maximumRadius) {
        color = ivec3(vec3(white, 0.0) * 2147483647.0);
        return;
    }

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
    vec3 result = vec3(white, lum);
    color = ivec3(result * 2147483647.0);
}
