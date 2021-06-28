uniform mat4 u_Matrix;
uniform float u_Time;

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector; // 粒子位移方向
attribute float a_ParticleStartTime; // 粒子开始运行时间

varying vec3 v_Color;
varying float v_ElapsedTime;

void main() {

    v_Color = a_Color;
    v_ElapsedTime = u_Time - a_ParticleStartTime; // 计算粒子运行时间
    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime); // 计算粒子位置
    gl_Position = u_Matrix * vec4(currentPosition, 1.0);
    gl_PointSize = 10.0;

}
