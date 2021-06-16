// 纹理片段着色器
#version 120
precision mediump float;

// 用于接收实际纹理数据，sampler2D指的是一个二维纹理数据的数组
uniform sampler2D u_TextureUnit;
// OpenGL会为每个片段调用此着色器
// 并接收从顶点着色器传入的v_TextureCoordinates
varying vec2 v_TextureCoordinates;

void main() {
    // 被插值的纹理坐标和纹理数据被传递给texture2D,用于读入纹理中特定坐标处的颜色值
    // 并将结果赋值给gl_FragColor上色
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
