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
uniform float exponentialConstant;
uniform sampler2D azimuthal;
uniform float numAzimuthalSubdivisions;
uniform sampler2D quadrature;
uniform float numQuadratureSubdivisions;
uniform int numQuadraturePoints;
uniform float M;
uniform float powerOfR;
uniform int colorMode;

float radialPart(float r) {
    return 1.5;
}

float azimuthalPart(float theta) {
        return 1.5;
}

vec2 quadratureData(float distanceToOrigin, int point) {
    return vec2(1.0, 1.0);
}

vec2 longitudinalPart(float phi) {
    return vec2(1.0, 0.0);
}

vec2 angularPart(vec3 x, float r) {
    float theta = acos(x.z / r); // 0 to pi
    // TODO this might make trouble if x.xy is small
    float phi = atan(0.0, 1.0); //atan(x.y, x.x); // -pi to pi
    return azimuthalPart(1.0) * longitudinalPart(phi);
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

    total *= pow(r, powerOfR) * exp(exponentialConstant * r) * radialValue * radialValue;
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
