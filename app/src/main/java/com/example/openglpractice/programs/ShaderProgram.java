package com.example.openglpractice.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.openglpractice.util.ShaderUtils;
import com.example.openglpractice.util.TextResourceUtil;

/**
 * 通过传入的着色器资源，编译连接成一个着色器程序
 * 子类在调用构造方法时，会去获取着色器程序中各类变量的地址
 * 并对变量进行值传递
 */
public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TIME = "u_Time";
    protected static final String U_TEXTURE_UNIT= "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int program;
    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        program = ShaderUtils.buildProgram(
                TextResourceUtil.readTextFileFromResource(
                        context, vertexShaderResourceId
                ),
                TextResourceUtil.readTextFileFromResource(
                        context, fragmentShaderResourceId
                )
        );
        useProgram();
    }

    public void useProgram(){
        // Set the current OpenGL shader program to this program.
        GLES20.glUseProgram(program);
    }

}
