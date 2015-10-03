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

float inclinationPart(float theta) {
    float c = cos(theta);
    return c * c;
}

float f(vec3 x) {
    float r = length(x);
    float theta = acos(x.z / r);
    return radialPart(r) * inclinationPart(theta);
}

void main() {
    vec3 span = back - front;

    float distanceFromOrigin = length(back - dot(back, span) / dot(span, span) * span);

    if (distanceFromOrigin > maximumRadius) {
        color = ivec3(vec3(white, 0.0) * 2147483647.0);
    } else {
        float total = 0.0;
        vec3 location = front;
        const int steps = 12;
        vec3 step = span / float(steps);
        int i = 0;
        total += f(location);
        ++i;
        location += step;
        total += 4.0 * f(location);
        ++i;
        while (i < steps) {
            location += step;
            total += 2.0 * f(location);
            ++i;
            location += step;
            total += 4.0 * f(location);
            ++i;
        }
        location += step;
        total += f(location);
        total *= length(step) / 3.0;

        total *= 0.75;

        vec3 result = vec3(white, total);
        color = ivec3(result * 2147483647.0);
    }
}
