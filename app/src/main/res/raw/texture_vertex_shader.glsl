// 纹理顶点着色器
#version 120
uniform mat4 u_Matrix;

attribute vec4 a_Position;
// 纹理坐标属性，有S，T两个分量
attribute vec2 a_TextureCoordinates;

// 通过差值器的方式传递
varying vec2 v_TextureCoordinates;

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
}
