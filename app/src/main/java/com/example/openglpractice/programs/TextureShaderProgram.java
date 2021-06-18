package com.example.openglpractice.programs;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.openglpractice.R;
import com.example.openglpractice.util.LogUtils;

/**
 * 纹理着色器程序
 */
public class TextureShaderProgram extends ShaderProgram {

    private static final String TAG = "TextureShaderProgram";

    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        LogUtils.d(TAG, "create TextureShaderProgram" + program);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        LogUtils.d(TAG, "uMatrixLocation: " + uMatrixLocation);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        LogUtils.d(TAG, "uTextureUnitLocation: " + uTextureUnitLocation);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        LogUtils.d(TAG, "aPositionLocation: " + aPositionLocation);

        aTextureCoordinatesLocation =
                GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        LogUtils.d(TAG, "aTextureCoordinatesLocation: " + aTextureCoordinatesLocation);

    }

    /**
     * 传递矩阵，纹理给uniform
     *
     * @param matrix
     * @param textureId
     */
    public void setUniforms(float[] matrix, int textureId) {
        GLES20.glUniformMatrix4fv(uMatrixLocation,
                1, false, matrix, 0);

        // 显卡中有N个纹理单元（具体数目依赖你的显卡能力），
        // 每个纹理单元（GL_TEXTURE0、GL_TEXTURE1等）都有GL_TEXTURE_1D、GL_TEXTURE_2D等
        // glActiveTexture 并不是激活纹理单元，而是选择当前活跃的纹理单元(默认为0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 把纹理textureId绑定到纹理单元0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 把纹理单元0传递给片段着色器的sampler2D
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }

}
