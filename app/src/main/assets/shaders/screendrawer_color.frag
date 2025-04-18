#version 300 es
precision highp int;
precision highp float;
precision highp isampler2D;
uniform isampler2D data;
uniform ivec2 upperClamp; // = ivec2(texSize) - ivec2(1)
uniform mat2 colorRotation;
uniform int colorBlindMode; // 1, 2, 3 for red, green, and blue blindness
in vec2 texCoord;
out vec3 color;

vec3 srgb_gamma(vec3 linear) {
    return mix(linear * 12.92,
               1.055 * pow(linear, vec3(1.0 / 2.4)) - vec3(0.055),
               greaterThan(linear, vec3(0.0031308)));
}

void main() {
    // This is what we want to do, but our texture is not filterable.
    //total = vec3(texture(data, texCoord).xyz);

    ivec2 leftBottom = ivec2(floor(texCoord));
    ivec2 rightTop = leftBottom + ivec2(1);
    leftBottom = clamp(leftBottom, ivec2(0, 0), upperClamp);
    rightTop = clamp(rightTop, ivec2(0, 0), upperClamp);

    vec3 lb = vec3(texelFetch(data, leftBottom, 0).xyz);
    vec3 lt = vec3(texelFetch(data, ivec2(leftBottom.x, rightTop.y), 0).xyz);
    vec3 rb = vec3(texelFetch(data, ivec2(rightTop.x, leftBottom.y), 0).xyz);
    vec3 rt = vec3(texelFetch(data, rightTop, 0).xyz);

    vec2 interp = fract(texCoord);
    // needs to be divided by 32767.0
    vec3 total = mix(mix(lb, rb, interp.x), mix(lt, rt, interp.x), interp.y);

    float t = texCoord.x * 2.0 * 3.141592653589;
    mat2 r = mat2(cos(t), sin(t), -sin(t), cos(t));
    vec2 uv_prime = 0.06 * (r * vec2(1,0));

    vec2 white = vec2(0.19784, 0.46832);

    if (colorBlindMode != 0) { // If color blind mode
        vec2 best_line;

        // The (x, y) coordinates of the copunctal points, from which confusion
        // lines emanate, are:
        // Protanopia:   (x, y) = (0.747, 0.253)
        // Deuteranopia: (x, y) = (1.4, -0.4)
        // Tritanopia:   (x, y) = (0.171, 0)
        // These have been converted to (u*, v*) coordinates below

        if (colorBlindMode == 1) {
            vec2 copunctal = vec2(0.657860, 0.501321); // protanopic copunctal point
            vec2 white_confusion = white - copunctal;
            best_line = vec2(-white_confusion.y, white_confusion.x);
            best_line /= length(best_line);
            uv_prime *= 1.05;
        } else if (colorBlindMode == 2) {
            vec2 copunctal = vec2(-1.217391, 0.782608); // deuteranopic copunctal point
            vec2 white_confusion = white - copunctal;
            best_line = vec2(-white_confusion.y, white_confusion.x);
            best_line /= length(best_line);
            uv_prime *= 1.04;
        } else if (colorBlindMode == 3) {
            vec2 copunctal = vec2(0.257336, 0); // tritanopic copunctal point
            vec2 white_confusion = white - copunctal;
            best_line = vec2(-white_confusion.y, white_confusion.x);
            best_line /= length(best_line);
            uv_prime *= 1.01;
        }

        // Project uv_prime onto best_line
        uv_prime = dot(uv_prime, best_line) * best_line;
    }

    float Y = total.z * (0.5 / 32767.0);
    Y = texCoord.y / 2.0;
    uv_prime += white;

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

    if (any(greaterThan(linear_RGB, vec3(1))) || any(lessThan(linear_RGB, vec3(0))))
        linear_RGB = vec3(1, 0, 1);

    // Need EGL 1.5 or EGL_KHR_gl_colorspace for automatic gamma
    color = srgb_gamma(linear_RGB);
}
