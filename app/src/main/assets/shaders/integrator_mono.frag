#version 300 es
precision highp int;
precision highp float;
precision highp sampler2D;

const float pi = 3.14159265358979;

in vec3 near, far;
out int color;

// Orbital
uniform sampler2D radial;
uniform float maximumRadius;
uniform float numRadialSubdivisions;
uniform float exponentialConstant;
uniform sampler2D azimuthal;
uniform float numAzimuthalSubdivisions;
uniform sampler2D quadrature;
uniform float numQuadratureSubdivisions;
uniform int numQuadraturePoints;
uniform float M;
uniform float powerOfR;
uniform bool realOrbital;
uniform float brightness;

float azimuthalPart(float theta) {
    float result;
    float positionInTexture = theta / pi * numAzimuthalSubdivisions;
    if (positionInTexture >= numAzimuthalSubdivisions) {
        result = texelFetch(azimuthal, ivec2(numAzimuthalSubdivisions, 0), 0).x;
    } else {
        float texturePosition = trunc(positionInTexture);
        vec2 textureValue = texelFetch(azimuthal, ivec2(texturePosition, 0), 0).xy;
        float interpolationValue = fract(positionInTexture);
        result = mix(textureValue.x, textureValue.y, interpolationValue);
    }
    return result;
}

vec2 quadratureData(float distanceToOrigin, int point) {
    float positionInTexture = distanceToOrigin / maximumRadius * numQuadratureSubdivisions;
    if (positionInTexture >= numQuadratureSubdivisions)
        return vec2(0.0);
    float texturePosition = trunc(positionInTexture);
    vec4 textureValue = texelFetch(quadrature, ivec2(point, texturePosition), 0);
    float interpolationValue = fract(positionInTexture);
    return mix(textureValue.xy, textureValue.zw, interpolationValue);
}

float longitudinalPart(float phi) {
    // Normalization constant so that this function times its conjugate,
    // integrated from 0 to 2pi, yields 1
    float result = 1.0 / sqrt(2.0 * pi);
    if (realOrbital && M != 0.0) {
        float Mphi = M * phi;
        const float sqrt2 = sqrt(2.0);
        if (M > 0.0)
            result *= sqrt2 * cos(Mphi);
        else // M < 0.0
            result *= sqrt2 * sin(Mphi);
    }
    return result;
}

float angularPart(vec3 x, float r) {
    float theta = acos(x.z / r); // 0 to pi
    float phi = atan(x.y, x.x); // -pi to pi (always numerically safe)
    return azimuthalPart(theta) * longitudinalPart(phi);
}

float integrand_pair(vec3 center, vec3 offset) {
    vec3 x = center - offset;
    float r = length(x);

    float result = angularPart(x, r);
    float total = result * result;

    x = center + offset;

    result = angularPart(x, r);
    total += result * result;

    total *= pow(r, powerOfR) * exp(exponentialConstant * r);
    return total;
}

void main() {
    vec3 ray = far - near;
    ray /= length(ray);

    // We need to find the point on the near <--> far line which is closest to
    // the origin.
    // dot(center, ray) = 0
    // center = near + t * ray
    // dot(near + t * ray, ray) = 0
    // dot(near, ray) + t * dot(ray, ray) = 0
    // dot(near, ray) + t = 0
    // t = - dot(near, ray)

    vec3 center = near - dot(near, ray) * ray;
    float distanceToOrigin = length(center);

    float total = 0.0;
    vec2 q;
    for (int i = 0; i < numQuadraturePoints; ++i) {
        q = quadratureData(distanceToOrigin, i);
        total += q.y * integrand_pair(center, q.x * ray);
    }

    if (total > 0.0) {
        // Increase brightness
        total *= brightness;

        // Galaxy S6 can't do exp of a negative number correctly
        total = 1.0 - 1.0 / exp(total);
        color = int(total * 32767.0);
    } else {
        color = 0;
    }
}
