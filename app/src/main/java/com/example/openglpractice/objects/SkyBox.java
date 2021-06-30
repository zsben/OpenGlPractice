package com.example.openglpractice.objects;

import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.SkyBoxShaderProgram;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

public class SkyBox {

    public static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public SkyBox() {
        // 顶点数组
        vertexArray = new VertexArray(new float[]{
                -1, 1, 1,
                1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                -1, 1, -1,
                1, 1, -1,
                -1, -1, -1,
                1, -1, -1
        });

        // 顶点索引数组
        indexArray = ByteBuffer.allocate(6 * 6)
                .put(new byte[]{
                        // Front
                        1, 3, 0,
                        0, 3, 2,
                        // Back
                        4, 5, 6,
                        5, 6, 7,
                        // Left
                        0, 2, 4,
                        4, 2, 6,
                        // Right
                        5, 7, 1,
                        1, 7, 3,
                        // Top
                        5, 1, 4,
                        4, 1, 0,
                        // Bottom
                        5, 2, 7,
                        7, 2, 3
                });
        indexArray.position(0);
    }

    public void bindData(SkyBoxShaderProgram skyBoxShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyBoxShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    /**
     * 使用索引数组，在vertexArray中进行取点绘制
     */
    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
    }

}
