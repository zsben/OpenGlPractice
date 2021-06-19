// 对基本图元（点，线，三角形）的每个片段，片段着色器都会被调用一次

// 浮点数据类型默认精度
precision mediump float;

// 传递一个uniform，会让每个顶点都用同一个值，除非再次改变它
// 也是一个四分向量，分别对应argb, 没有默认值
uniform vec4 u_Color;

void main(){
    // 把uniform里定义的颜色赋值到gl_FragColor
    // OpenGL会用gl_FragColor作为当前片段的最终颜色
    // 用这个uniform设置将要绘制的东西的颜色
    // gl_FragColor = u_Color;

    // 代替原来的u_Color
    gl_FragColor = u_Color;
}