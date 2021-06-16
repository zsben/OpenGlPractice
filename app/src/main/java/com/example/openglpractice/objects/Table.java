package com.example.openglpractice.objects;

import android.opengl.GLES20;

import com.example.openglpractice.Constants;
import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.TextureShaderProgram;

public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;  // 坐标分量
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2; // 纹理坐标分量
    private static final int STRIDE = (POSITION_COMPONENT_COUNT  // 步长
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    // 顶点数据, 对上下进行裁剪
    private static final float[] VERTEX_DATA = {
            //  x,   y,    s,  t
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.f
    };

    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    /**
     * 将顶点数组绑定到纹理着色器上
     */
    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    /**
     * 通知OpenGL进行绘制
     */
    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }

}
