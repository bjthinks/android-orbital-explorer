#version 300 es
precision highp int;
precision highp float;
precision highp sampler2D;

const float pi = 3.14159265358979;

in vec3 near, far;
out ivec3 color;

uniform sampler2D radial;
uniform sampler2D azimuthal;
uniform sampler2D quadrature;

uniform float fQuadratureRadius;
uniform float fMaximumRadius;
uniform float fNumRadialSubdivisions;
uniform float fExponentialConstant;
uniform float fNumAzimuthalSubdivisions;
uniform float fNumQuadratureSubdivisions;
uniform int numQuadraturePoints;
uniform float M;
uniform float powerOfR;
uniform bool realOrbital;
uniform float brightness;

float radialPart(float r) {
    float positionInTexture = r / fMaximumRadius * fNumRadialSubdivisions;
    if (positionInTexture >= fNumRadialSubdivisions)
        return 0.0;
    float texturePosition = trunc(positionInTexture);
    vec2 textureValue = texelFetch(radial, ivec2(texturePosition, 0), 0).xy;
    float interpolationValue = fract(positionInTexture);
    return mix(textureValue.x, textureValue.y, interpolationValue);
}

float azimuthalPart(float theta) {
    float result;
    float positionInTexture = theta / pi * fNumAzimuthalSubdivisions;
    if (positionInTexture >= fNumAzimuthalSubdivisions) {
        result = texelFetch(azimuthal, ivec2(fNumAzimuthalSubdivisions, 0), 0).x;
    } else {
        float texturePosition = trunc(positionInTexture);
        vec2 textureValue = texelFetch(azimuthal, ivec2(texturePosition, 0), 0).xy;
        float interpolationValue = fract(positionInTexture);
        result = mix(textureValue.x, textureValue.y, interpolationValue);
    }
    return result;
}

vec2 quadratureData(float distanceToOrigin, int point) {
    float positionInTexture = distanceToOrigin / fQuadratureRadius * fNumQuadratureSubdivisions;
    if (positionInTexture >= fNumQuadratureSubdivisions)
        return vec2(0.0);
    float texturePosition = trunc(positionInTexture);
    vec4 textureValue = texelFetch(quadrature, ivec2(point, texturePosition), 0);
    float interpolationValue = fract(positionInTexture);
    return mix(textureValue.xy, textureValue.zw, interpolationValue);
}

vec2 longitudinalPart(float phi) {
    vec2 result;
    if (M == 0.0) {
        result = vec2(1.0, 0.0);
    } else {
        float Mphi = M * phi;
        if (realOrbital) {
            const float sqrt2 = sqrt(2.0);
            if (M > 0.0)
                result = vec2(sqrt2 * cos(Mphi), 0.0);
            else // M < 0.0
                result = vec2(sqrt2 * sin(Mphi), 0.0);
        } else {
            result = vec2(cos(Mphi), sin(Mphi));
        }
    }
    // Normalization constant so that this function times its conjugate,
    // integrated from 0 to 2pi, yields 1
    const float oneOverSqrt2PI = 1.0 / sqrt(2.0 * pi);
    return result * oneOverSqrt2PI;
}

vec2 angularPart(vec3 x, float r) {
    float theta = acos(x.z / r); // 0 to pi
    float phi = atan(x.y, x.x); // -pi to pi (always numerically safe)
    return azimuthalPart(theta) * longitudinalPart(phi);
}

vec3 integrand_pair(vec3 center, vec3 offset) {
    vec3 x = center - offset;
    float r = length(x);
    float radialValue = radialPart(r);
    float radialSign = sign(radialValue);

    vec2 result = angularPart(x, r);
    float len = length(result);
    vec3 total = len * vec3(radialSign * result, len);

    x = center + offset;

    result = angularPart(x, r);
    len = length(result);
    total += len * vec3(radialSign * result, len);

    total *= pow(r, powerOfR) * exp(fExponentialConstant * r) * radialValue * radialValue;
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

    vec3 total = vec3(0);
    vec2 q;
    for (int i = 0; i < numQuadraturePoints; ++i) {
        q = quadratureData(distanceToOrigin, i);
        total += q.y * integrand_pair(center, q.x * ray);
    }

    if (total.z > 0.0) {
        // Increase brightness
        total *= brightness;

        total.xy /= total.z;
        // Galaxy S6 can't do exp of a negative number correctly
        total.z = 1.0 - 1.0 / exp(total.z);
        color = ivec3(total * 32767.0);
    } else {
        color = ivec3(0);
    }
}
