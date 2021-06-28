package com.example.openglpractice.programs;

import android.content.Context;

import com.example.openglpractice.R;
import com.example.openglpractice.util.LogUtils;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ParticleShaderProgram extends ShaderProgram{

    public final String TAG = "ParticleShaderProgram";

    private final int uMatrixLocation; // 投影矩阵
    private final int uTimeLocation; // 当前时间

    private final int aPositionLocation; // 坐标
    private final int aColorLocation; // 颜色
    private final int aDirectionVectorLocation; // 速度矢量
    private final int aParticleStartTimeLocation; // 被创建的时间

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader,
                R.raw.particle_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation =
                glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
    }

    public int getPositionAttributeLocation(){
        LogUtils.d(TAG, "aPositionLocation: " + aPositionLocation);
        return aPositionLocation;
    }

    public int getColorAttributeLocation(){
        LogUtils.d(TAG, "aColorLocation: " + aColorLocation);
        return aColorLocation;
    }

    public int getDirectionVectorLocation(){
        LogUtils.d(TAG, "aDirectionVectorLocation: " + aDirectionVectorLocation);
        return aDirectionVectorLocation;
    }

    public int getParticleStartTimeLocation(){
        LogUtils.d(TAG, "aParticleStartTimeLocation: " + aParticleStartTimeLocation);
        return aParticleStartTimeLocation;
    }

}
