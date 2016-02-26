#version 300 es
precision highp int;
precision highp float;
uniform isampler2D data;
uniform ivec2 upperClamp; // = ivec2(texSize) - ivec2(1)
in vec2 texCoord;
out vec3 color;

float srgb_gamma(float linear) {
    float result;
    if (linear > 0.0031308)
        result = 1.055 * pow(linear, 1.0 / 2.4) - 0.055;
    else
        result = linear * 12.92;
    return result;
}

void main() {
    // This is what we want to do, but our texture is not filterable.
    //total = vec3(texture(data, texCoord).xyz);

    ivec2 leftBottom = ivec2(floor(texCoord));
    ivec2 rightTop = leftBottom + ivec2(1);
    leftBottom = clamp(leftBottom, ivec2(0, 0), upperClamp);
    rightTop = clamp(rightTop, ivec2(0, 0), upperClamp);

    float lb = float(texelFetch(data, leftBottom, 0).x);
    float lt = float(texelFetch(data, ivec2(leftBottom.x, rightTop.y), 0).x);
    float rb = float(texelFetch(data, ivec2(rightTop.x, leftBottom.y), 0).x);
    float rt = float(texelFetch(data, rightTop, 0).x);

    vec2 interp = fract(texCoord);
    // needs to be divided by 32767.0
    float total = mix(mix(lb, rb, interp.x), mix(lt, rt, interp.x), interp.y);

    float linear_brightness = total * (0.5 / 32767.0);

    vec3 result;
    if (linear_brightness > 1.0 || linear_brightness < 0.0)
        result = vec3(1, 0, 1);
    else
        result = vec3(srgb_gamma(linear_brightness));
    color = result;
}
