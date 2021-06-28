package com.example.openglpractice.objects;

import com.example.openglpractice.util.Geometry.*;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

public class ParticleShooter {

    private final Point position;
    private final Vector direction;
    private final int color;

    private final float angleVariance;
    private final float speedVariance;

    private final Random random = new Random();

    private float[] rotationMatrix = new float[16]; // 随机旋转角度矩阵
    private float[] directionVector = new float[4]; // 初始方向向量
    private float[] resultVector = new float[4]; // 计算后的方向向量

    public ParticleShooter(Point position, Vector direction, int color,
                           float angleVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;

        directionVector[0] = direction.x; // 设置初始方向向量
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void addParticles(ParticleSystem particleSystem, float currentTime,
                             int count) {
        for (int i = 0; i < count; i++) {
            particleSystem.addParticle(position, color, direction, currentTime);
            // 生成一个随机矩阵改变发射角度
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);

            // 将旋转矩阵与远方向相乘
            multiplyMV(resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0);

            float speedAdjustment = 1f + random.nextFloat() * speedVariance;

            Vector thisDirection = new Vector(
                    resultVector[0] * speedAdjustment,
                    resultVector[1] * speedAdjustment,
                    resultVector[2] * speedAdjustment
            );
            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }

}
