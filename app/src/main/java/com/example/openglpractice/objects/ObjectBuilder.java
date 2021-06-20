package com.example.openglpractice.objects;

import android.util.FloatMath;

import com.example.openglpractice.util.Geometry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

public class ObjectBuilder {

    interface DrawCommand { // 绘制命令接口
        void draw();
    }

    /**
     * build后的dataHolder
     */
    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;   // 顶点坐标数据
    private int offset = 0; // 数据偏移量

    private final List<DrawCommand> drawList = new ArrayList<>(); // 绘制命令列表

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints) { // 计算圆上顶点数
        return 1 + (numPoints + 1); // 圆心点+圆周点
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) { // 计算圆柱上顶点数
        return (numPoints + 1) * 2; // 圆周点 * 2
    }

    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) { // 用圆柱体创造冰球顶点数据
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        Geometry.Circle puckTop = new Geometry.Circle(
                puck.center.translateY(puck.height / 2f), // 将圆上移半个圆柱高度，用于封顶
                puck.radius
        );

        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    // 创建一个木槌顶点数据，手柄高度75%，基部高度25%，基部宽度是手柄三倍
    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

        // 计算基部图形
        float baseHeight = height * 0.25f;

        Geometry.Circle baseCircle = new Geometry.Circle( // 基部顶部圆，木槌中心下移baseHeight
                center.translateY(-baseHeight),
                radius
        );
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder( // 基部圆柱体，再下移baseHeight/2
                baseCircle.center.translateY(-baseHeight / 2f),
                radius,
                baseHeight
        );
        // 将基部加入顶点集
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        // 计算顶部图形
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Geometry.Circle handleCircle = new Geometry.Circle( // 手柄顶部圆，木槌中心上移height/2
                center.translateY(height / 2f),
                radius
        );
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius,
                handleHeight
        );
        // 将顶部加入顶点即
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    /**
     * 添加一个圆,将顶点数据添加到vertexData中，绘制命令添加到drawList中
     * @param circle
     * @param numPoints
     */
    private void appendCircle(Geometry.Circle circle, int numPoints) {

        final int startVertex = offset / FLOATS_PER_VERTEX; // 绘制起始顶点数
        final int numVertices = sizeOfCircleInVertices(numPoints); // 绘制顶点个数

        // 添加圆心
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // 将圆切成三角形扇添加顶点
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadius = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadius);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadius);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    /**
     * 添加一个圆柱侧面，将顶点数据添加到vertexData中，绘制命令添加到drawList中
     * @param cylinder
     * @param numPoints
     */
    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) { // 添加一个圆柱,将绘制命令添加到drawList中

        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        // 将圆柱侧面分割成三角形带
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

}
