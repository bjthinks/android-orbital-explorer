#version 300 es
precision highp int;
precision highp float;
precision highp sampler2D;

const float pi = 3.14159265358979;

in vec3 near, far;
out int color;

uniform sampler2D radial;
uniform sampler2D azimuthal;
uniform sampler2D quadrature;

uniform bool bReal;
uniform float fBrightness;
uniform float fInverseAzimuthalStepSize;
uniform float fInverseQuadratureStepSize;
uniform float fInverseRadialStepSize; // unused
uniform float fM;
uniform float fRadialExponent;
uniform float fRadialPower;
uniform int iAzimuthalSteps;
uniform int iOrder;
uniform int iQuadratureSteps;
uniform int iRadialSteps; // unused

float azimuthalPart(float theta) {
    float positionInTexture = theta * fInverseAzimuthalStepSize;
    int texturePosition = int(trunc(positionInTexture));
    if (texturePosition >= iAzimuthalSteps)
        return texelFetch(azimuthal, ivec2(iAzimuthalSteps - 1, 0), 0).y;
    vec2 textureValue = texelFetch(azimuthal, ivec2(texturePosition, 0), 0).xy;
    float interpolationValue = fract(positionInTexture);
    return mix(textureValue.x, textureValue.y, interpolationValue);
}

vec2 quadratureData(float positionInTexture, int point) {
    int texturePosition = int(trunc(positionInTexture));
    vec4 textureValue = texelFetch(quadrature, ivec2(point, texturePosition), 0);
    float interpolationValue = fract(positionInTexture);
    return mix(textureValue.xy, textureValue.zw, interpolationValue);
}

float longitudinalPart(float phi) {
    // Normalization constant so that this function times its conjugate,
    // integrated from 0 to 2pi, yields 1
    float result = 1.0 / sqrt(2.0 * pi);
    if (bReal && fM != 0.0) {
        float Mphi = fM * phi;
        const float sqrt2 = sqrt(2.0);
        if (fM > 0.0)
            result *= sqrt2 * cos(Mphi);
        else // M < 0.0
            result *= sqrt2 * sin(Mphi);
    }
    return result;
}

float angularPart(vec3 x, float r) {
    float theta = acos(x.z / r); // 0 to pi
    if (isnan(theta))
        theta = 0.0;
    float phi = atan(x.y, x.x); // -pi to pi (always numerically safe)
    if (isnan(phi))
        phi = 0.0;
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

    float factor = pow(r, fRadialPower) * exp(r * fRadialExponent);
    total *= factor * factor;
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

    float positionInTexture = distanceToOrigin * fInverseQuadratureStepSize;
    float total = 0.0;
    if (int(trunc(positionInTexture)) < iQuadratureSteps) {
        vec2 q;
        for (int i = 0; i < iOrder; ++i) {
            q = quadratureData(positionInTexture, i);
            total += q.y * integrand_pair(center, q.x * ray);
        }
    }

    if (total > 0.0) {
        // Increase brightness
        total *= fBrightness;

        // Galaxy S6 can't do exp of a negative number correctly
        total = 1.0 - 1.0 / exp(total);
        color = int(total * 32767.0);
    } else {
        color = 0;
    }
}
