#version 300 es
precision highp int;
precision highp float;

const float pi = 3.14159265358979;

in vec3 near, far;
out ivec3 color;

// Orbital
uniform sampler2D radial;
uniform float maximumRadius;
uniform float numRadialSubdivisions;
uniform sampler2D azimuthal;
uniform float numAzimuthalSubdivisions;
uniform sampler2D quadrature;
uniform sampler2D quadrature2;
uniform float numQuadratureSubdivisions;
uniform float M;
uniform int colorMode;

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
    float result;
    float positionInTexture = theta / pi * numAzimuthalSubdivisions;
    if (positionInTexture >= numAzimuthalSubdivisions) {
        float rightTexturePosition = numAzimuthalSubdivisions;
        float rightTextureValue = texelFetch(azimuthal, ivec2(rightTexturePosition, 0), 0).x;
        result = rightTextureValue;
    } else {
        float leftTexturePosition = trunc(positionInTexture);
        float leftTextureValue = texelFetch(azimuthal, ivec2(leftTexturePosition, 0), 0).x;
        float rightTexturePosition = leftTexturePosition + 1.0;
        float rightTextureValue = texelFetch(azimuthal, ivec2(rightTexturePosition, 0), 0).x;
        float interpolationValue = fract(positionInTexture);
        result = mix(leftTextureValue, rightTextureValue, interpolationValue);
    }
    return result;
}

vec4 quadratureData(float distanceToOrigin) {
    float positionInTexture = distanceToOrigin / maximumRadius * numQuadratureSubdivisions;
    if (positionInTexture >= numQuadratureSubdivisions)
        return vec4(0.0);
    float leftTexturePosition = trunc(positionInTexture);
    vec4 leftTextureValue = texelFetch(quadrature, ivec2(leftTexturePosition, 0), 0);
    float rightTexturePosition = leftTexturePosition + 1.0;
    vec4 rightTextureValue = texelFetch(quadrature, ivec2(rightTexturePosition, 0), 0);
    float interpolationValue = fract(positionInTexture);
    return mix(leftTextureValue, rightTextureValue, interpolationValue);
}
vec4 quadratureData2(float distanceToOrigin) {
    float positionInTexture = distanceToOrigin / maximumRadius * numQuadratureSubdivisions;
    if (positionInTexture >= numQuadratureSubdivisions)
        return vec4(0.0);
    float leftTexturePosition = trunc(positionInTexture);
    vec4 leftTextureValue = texelFetch(quadrature2, ivec2(leftTexturePosition, 0), 0);
    float rightTexturePosition = leftTexturePosition + 1.0;
    vec4 rightTextureValue = texelFetch(quadrature2, ivec2(rightTexturePosition, 0), 0);
    float interpolationValue = fract(positionInTexture);
    return mix(leftTextureValue, rightTextureValue, interpolationValue);
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

    vec3 total;
    vec4 q;
    q = quadratureData(distanceToOrigin);
    total  = q.y * (integrand(center + q.x * ray) + integrand(center - q.x * ray));
    total += q.w * (integrand(center + q.z * ray) + integrand(center - q.z * ray));
    q = quadratureData2(distanceToOrigin);
    total += q.y * (integrand(center + q.x * ray) + integrand(center - q.x * ray));
    total += q.w * (integrand(center + q.z * ray) + integrand(center - q.z * ray));

    total *= 50.0;

    if (total.z > 0.0) {
        if (colorMode == 1)
            total.xy = vec2(0);
        else if (colorMode == 2) {
            float angle = pi * 4.0 / 9.0;
            vec2 good = vec2(cos(angle), sin(angle));
            total.xy = good * dot(total.xy, good);
        }
        float totalScaleFactor = (1.0 - exp(-total.z)) / total.z;
        total *= totalScaleFactor;
        color = ivec3(total * 2147483647.0);
    } else {
        color = ivec3(0);
    }
}
