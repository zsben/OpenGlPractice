precision mediump float;

uniform samplerCube u_TextureUnit;
varying vec3 v_Position;

void main() {
    // 把被插值的立方体面的位置作为片段的纹理坐标
    gl_FragColor = textureCube(u_TextureUnit, v_Position);
}
