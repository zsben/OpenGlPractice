precision mediump float;

varying vec3 v_Color;
varying float v_ElapsedTime;

uniform sampler2D u_TextureUnit;

void main() {

    // 对于每个点，当调用片段着色器时，都会得到一个二维的gl_PointCoord坐标空间轴
    // 在每个轴上，其分量的范围都是从0到1，其取值依赖于点上的哪个片段当前正在被渲染
    // 渲染每个点时，相对于gl_PointCoord上的每个轴来说，其片段的位置范围都是从0到1
    // 因此将点的圆心放到（0.5, 0.5）， 并绘制之那些位于半径内的片段
    /*float xDistance = 0.5 - gl_PointCoord.x;
    float tDistance = 0.5 - gl_PointCoord.y;
    float distanceFromCenter =
        sqrt(xDistance * xDistance + tDistance * tDistance); // 求出片段到圆心距离

    if (distanceFromCenter > 0.5) { // 如果片段在圆外，则丢弃
        discard;
    } else { // 否则绘制
        gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
    }*/

    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0)
        * texture2D(u_TextureUnit, gl_PointCoord);
}
