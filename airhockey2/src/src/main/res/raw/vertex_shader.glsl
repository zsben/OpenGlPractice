// 对于每一个单独定义过的顶点，顶点着色器都会被调用一次；
// 当它被调用的时候，它会在a_Position属性里接收当前定点的位置, 这个属性被定义成vec4类型
// vec4包含4个分量：x,y,z,w，默认x,y,z=0，w=1
// attribute就是把java文件中属性放进着色器的手段
attribute vec4 a_Position;


void main(){
    // 必须给gl_Position赋值，OpenGL会把gl_Position中存储的值作为当前顶点最终位置
    gl_Position = a_Position;
    // 调整点的大小
    gl_PointSize = 100.0;
}