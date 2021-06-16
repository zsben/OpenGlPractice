package com.example.openglpractice.data;

import android.opengl.GLES20;

import com.example.openglpractice.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 用于封装存储定点矩阵
 */
public class VertexArray {

    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    /**
     * 关联着色器属性与数据
     * @param dataOffset 首偏移量
     * @param attributeLocation 着色器属性
     * @param componentCount 着色器属性长度
     * @param stride 步长
     */
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
                false, stride, floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }

}
