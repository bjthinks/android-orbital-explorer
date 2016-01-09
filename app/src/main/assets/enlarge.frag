#version 300 es
precision highp int;
precision highp float;
uniform usampler2D data;
uniform vec2 texSize;
in vec2 boxCoord;
out vec3 color;

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
    color = mix(mix(lb, rb, interp.x), mix(lt, rt, interp.x), interp.y) / 65535.0;
}
