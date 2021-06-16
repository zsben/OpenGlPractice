package com.example.openglpractice.objects;

import android.opengl.GLES20;

import com.example.openglpractice.Constants;
import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.ColorShaderProgram;

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 2;  // 顶点坐标属性维度
    private static final int COLOR_COMPONENT_COUNT = 3; // 顶点颜色属性维度
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            0f, -0.4f,  0f, 0f, 1f,
            0f, 0.4f,   1f, 0f, 0f
    };

    private final VertexArray vertexArray;

    public Mallet(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2);
    }
}
