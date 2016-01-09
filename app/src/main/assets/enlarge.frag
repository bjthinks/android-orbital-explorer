#version 300 es
precision highp int;
precision highp float;
uniform usampler2D data;
uniform vec2 texSize;
in vec2 boxCoord;
out vec3 color;

vec3 srgb_gamma(vec3 linear) {
    return mix(linear * 12.92,
               1.055 * pow(linear, vec3(1.0 / 2.4)) - vec3(0.055),
               greaterThan(linear, vec3(0.0031308)));
}

void main() {
    // This is what we want to do, but our texture is not filterable.
    //color = vec3(texture(data, boxCoord).xyz);

    vec2 texCoord = boxCoord * texSize - vec2(0.5);
    ivec2 leftBottom = ivec2(floor(texCoord));
    ivec2 rightTop = leftBottom + ivec2(1);
    leftBottom = clamp(leftBottom, ivec2(0, 0), ivec2(texSize));
    rightTop = clamp(rightTop, ivec2(0, 0), ivec2(texSize));

    vec3 lb = vec3(texelFetch(data, leftBottom, 0).xyz);
    vec3 lt = vec3(texelFetch(data, ivec2(leftBottom.x, rightTop.y), 0).xyz);
    vec3 rb = vec3(texelFetch(data, ivec2(rightTop.x, leftBottom.y), 0).xyz);
    vec3 rt = vec3(texelFetch(data, rightTop, 0).xyz);

    vec2 interp = fract(texCoord);
    vec3 linear_RGB = mix(mix(lb, rb, interp.x), mix(lt, rt, interp.x), interp.y) / 65535.0;

    // Need EGL 1.5 or EGL_KHR_gl_colorspace for automatic gamma
    color = srgb_gamma(linear_RGB);
}
