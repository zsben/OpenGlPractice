package com.example.openglpractice.objects;

import android.graphics.Color;

import com.example.openglpractice.Constants;
import com.example.openglpractice.data.VertexArray;
import com.example.openglpractice.programs.ParticleShaderProgram;
import com.example.openglpractice.util.Geometry.*;
import com.example.openglpractice.util.LogUtils;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

public class ParticleSystem {

    public static final String TAG = "ParticleSystem";

    private static final int POSITION_COMPONENT_COUNT = 3; // 坐标
    private static final int COLOR_COMPONENT_COUNT = 3; // 颜色
    private static final int VECTOR_COMPONENT_COUNT = 3; // 速度
    private static final int PARTICLE_START_COMPONENT_COUNT = 1; // 被创建时间

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
                    + COLOR_COMPONENT_COUNT
                    + VECTOR_COMPONENT_COUNT
                    + PARTICLE_START_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final float[] particles; // 粒子数据
    private final VertexArray vertexArray; // 顶点数据
    private final int maxParticleCount; // 最大粒子数

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Point position, int color, Vector direction,
                            float particleStartTime) {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }

        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        // 将数据更新到buffer中
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleShaderProgram) {
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getParticleStartTimeLocation(),
                PARTICLE_START_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
        int offset = 0;
        for (int i = 0; i < currentParticleCount; i++) {
            LogUtils.d(TAG, "position: " + particles[offset++] + " " + particles[offset++] + " " + particles[offset++]);
            LogUtils.d(TAG, "color: " + particles[offset++] + " " + particles[offset++] + " " + particles[offset++]);
            LogUtils.d(TAG, "direction: " + particles[offset++] + " " + particles[offset++] + " " + particles[offset++]);
            LogUtils.d(TAG, "particleStartTime: " + particles[offset++]);
        }
    }

}
