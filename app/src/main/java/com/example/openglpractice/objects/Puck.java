package com.example.openglpractice.objects;

import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.ColorShaderProgram;
import com.example.openglpractice.util.Geometry;

import java.util.List;

public class Puck {

    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawCommandList;

    /**
     * 生成一个冰球的顶点数据与绘制命令
     * @param radius
     * @param height
     * @param numPointsAroundPuck
     */
    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height),
                numPointsAroundPuck
        );
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommandList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand: drawCommandList) {
            drawCommand.draw();
        }
    }

}
