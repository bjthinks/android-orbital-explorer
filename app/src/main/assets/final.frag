#version 300 es
precision highp float;
uniform sampler2D data;
in vec2 texCoord;
out vec3 color;

vec3 srgb_gamma(vec3 linear) {
    return mix(linear * 12.92,
               1.055 * pow(linear, vec3(1.0 / 2.4)) - vec3(0.055),
               greaterThan(linear, vec3(0.0031308)));
}

void main() {
    vec4 blet = texture(data, texCoord);

    vec2 uv_prime = blet.xy;
    float Y = blet.z;

    // Convert CIE (u',v') color coordinates (as per CIELUV) to (x,y)
    vec2 xy = vec2(9.0, 4.0) * uv_prime;
    xy /= dot(vec3(6.0, -16.0, 12.0), vec3(uv_prime, 1.0));

    // Add z, defined as 1 - x - y
    vec3 xyz = vec3(xy, 1.0 - xy.x - xy.y);

    // Convert xyz to XYZ
    vec3 XYZ = (Y / xyz.y) * xyz;

    // Convert XYZ to linear (i.e. pre-gamma) RGB values
    mat3 XYZ_to_linear_RGB = mat3( 3.2406, -0.9689,  0.0557,
                                  -1.5372,  1.8758, -0.2040,
                                  -0.4986,  0.0415,  1.0570);
    vec3 linear_RGB = XYZ_to_linear_RGB * XYZ;

    if (linear_RGB.r < 0.0 || linear_RGB.r > 1.0 ||
        linear_RGB.g < 0.0 || linear_RGB.g > 1.0 ||
        linear_RGB.b < 0.0 || linear_RGB.b > 1.0)
        linear_RGB = vec3(0, 0, 0);

    // Need EGL 1.5 or EGL_KHR_gl_colorspace to do this automatically
    color = srgb_gamma(linear_RGB);
}
