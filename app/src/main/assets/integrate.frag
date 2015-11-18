#version 300 es
precision highp int;
precision highp float;

const float pi = 3.14159265358979;

in vec3 front, back;
out ivec3 color;

// Orbital
uniform sampler2D radial;
const float maximumRadius = 16.0;
const float numRadialSubdivisions = 1024.0;
uniform sampler2D azimuthal;
const float numAzimuthalSubdivisions = 1024.0;
uniform float M;

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

float azimuthalPart(float theta) {
    float positionInTexture = theta / pi * numAzimuthalSubdivisions;
    if (positionInTexture >= numAzimuthalSubdivisions)
        // TODO handle this case more robustly
        positionInTexture = numAzimuthalSubdivisions - 0.01;
    float leftTexturePosition = trunc(positionInTexture);
    float leftTextureValue = texelFetch(azimuthal, ivec2(leftTexturePosition, 0), 0).x;
    float rightTexturePosition = leftTexturePosition + 1.0;
    float rightTextureValue = texelFetch(azimuthal, ivec2(rightTexturePosition, 0), 0).x;
    float interpolationValue = fract(positionInTexture);
    return mix(leftTextureValue, rightTextureValue, interpolationValue);
    // was: return 1.0 / sqrt(2.0);
}

vec2 longitudinalPart(float phi) {
    // Normalization constant so that this function times its conjugate,
    // integrated from 0 to 2pi, yields 1
    const float oneOverSqrt2PI = 1.0 / sqrt(2.0 * pi);
    float Mphi = M * phi;
    return vec2(cos(Mphi), sin(Mphi)) * oneOverSqrt2PI;
}

vec2 wavefunction(vec3 x) {
    float r = length(x);
    float theta = acos(x.z / r); //   0 to pi
    // TODO this might make trouble if x.xy is small
    float phi = atan(x.y, x.x);  // -pi to pi
    return radialPart(r) * azimuthalPart(theta) * longitudinalPart(phi);
}

vec3 integrand(vec3 x) {
    vec2 w = wavefunction(x);
    float len = length(w);
    return len * vec3(w, len);
}

void main() {
    // Given: a parametric line as f(t) = position + t * direction, and a radius
    // Then, the t values where the line intersects the sphere of the given radius
    // centered at the origin are:
    // t = (-dot(position, direction) +/- sqrt(dot(position, direction)^2 -
    //      dot(direction, direction) * (dot(position, position) - radius^2))) /
    //     dot(direction, direction)

    vec3 span = back - front;

    float distanceFromOrigin = length(back - dot(back, span) / dot(span, span) * span);

    if (distanceFromOrigin > maximumRadius) {
        color = ivec3(0);
    } else {
        vec3 total = vec3(0.0, 0.0, 0.0);
        vec3 location = front;
        const int steps = 32;
        vec3 step = span / float(steps);
        int i = 0;
        total += integrand(location);
        ++i;
        while (i < steps) {
            location += step;
            total += 2.0 * integrand(location);
            ++i;
        }
        location += step;
        total += integrand(location);
        total *= length(step) / 2.0;

        total *= 50.0;

        vec3 spam;
        if (total.z > 0.0) {
            float totalScaleFactor = (1.0 - exp(-total.z)) / total.z;
            total *= totalScaleFactor;
            spam = vec3(total.xy / total.z, total.z);
            color = ivec3(spam * 2147483647.0);
        } else {
            color = ivec3(0);
        }
    }
}
