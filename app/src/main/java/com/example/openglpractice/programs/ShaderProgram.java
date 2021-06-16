package com.example.openglpractice.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.openglpractice.util.ShaderUtils;
import com.example.openglpractice.util.TextResourceUtil;

public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

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
    }

    public void useProgram(){
        // Set the current OpenGL shader program to this program.
        GLES20.glUseProgram(program);
    }

}