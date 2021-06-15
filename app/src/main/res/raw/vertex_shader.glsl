// 对于每一个单独定义过的顶点，顶点着色器都会被调用一次；
// 当它被调用的时候，它会在a_Position属性里接收当前定点的位置, 这个属性被定义成vec4类型
// vec4包含4个分量：x,y,z,w，默认x,y,z=0，w=1
// attribute就是把java文件中属性放进着色器的手段
attribute vec4 a_Position;

attribute vec4 a_Color;

// 特殊变量类型，它把给它的值进行混合，并将混合后的值送给片段着色器
varying vec4 v_Color;

void main(){
    // 通过把a_Color赋值给v_Color，告诉OpenGL需要每个片段都接收一个混合后的颜色
    v_Color = a_Color;
    // 必须给gl_Position赋值，OpenGL会把gl_Position中存储的值作为当前顶点最终位置
    gl_Position = a_Position;
    // 调整点的大小
    gl_PointSize = 100.0;
}