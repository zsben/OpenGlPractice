package com.example.openglpractice.objects;

import android.opengl.GLES20;

import com.example.openglpractice.Constants;
import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.ColorShaderProgram;
import com.example.openglpractice.util.Geometry;

import java.util.List;

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;  // 顶点坐标属性维度

    public final float radius;
    public final float height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawCommandList;

    /**
     * 生成一个木槌的所有数据
     * @param radius
     * @param height
     * @param numPointsAroundMallet
     */
    public Mallet(float radius, float height, int numPointsAroundMallet){
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(
                new Geometry.Point(0f, 0f, 0f),
                radius,
                height,
                numPointsAroundMallet
        );

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommandList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw(){
        for (ObjectBuilder.DrawCommand drawCommand: drawCommandList) {
            drawCommand.draw();
        }
    }
}
