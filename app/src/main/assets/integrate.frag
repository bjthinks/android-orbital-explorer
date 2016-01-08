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

vec2 angularPart() {
    float phi = atan(0.0, 1.0); //atan(x.y, x.x); // -pi to pi
    return vec2(1.0, 0.0);
}

vec3 integrand_pair(vec3 center, vec3 offset) {
    return vec3(0.0);
}

void main() {
    vec3 total = vec3(0);
    vec2 q;
    for (int i = 0; i < numQuadraturePoints; ++i) {
        total += vec3(angularPart(), 0.0);
    }

    float totalScaleFactor = (1.0 - exp(-total.z)) / total.z;
    total *= totalScaleFactor;
    color = ivec3(total * 2147483647.0);
}
