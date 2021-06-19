// 对于每一个单独定义过的顶点，顶点着色器都会被调用一次；
// 当它被调用的时候，它会在a_Position属性里接收当前定点的位置, 这个属性被定义成vec4类型
// vec4包含4个分量：x,y,z,w，默认x,y,z=0，w=1
// attribute就是把java文件中属性放进着色器的手段
attribute vec4 a_Position;

// 正交投影矩阵
uniform mat4 u_Matrix;

void main(){
    // 必须给gl_Position赋值，OpenGL会把gl_Position中存储的值作为当前顶点最终位置
    // 使用正交投影映射坐标
    gl_Position = u_Matrix * a_Position;
}