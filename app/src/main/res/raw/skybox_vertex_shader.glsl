uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Position;

void main() {
    v_Position = a_Position; // 将顶点位置传递给片段着色器
    v_Position.z = - v_Position.z; // 反转z轴，将右手坐标转换为左手坐标

    gl_Position = u_Matrix * vec4(a_Position, 1.0);
    gl_Position = gl_Position.xyww; // 将z分量设置成w分量，另透视触除法后的z永远为1
}
