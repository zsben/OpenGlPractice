package com.example.openglpractice.programs;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.openglpractice.R;
import com.example.openglpractice.util.LogUtils;

public class ColorShaderProgram extends ShaderProgram {

    private final static String TAG = "ColorShaderProgram";

    private final int uMatrixLocation;
    private final int uColorLocation;

    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment);

        LogUtils.d(TAG, "create ColorShaderProgram" + program);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        LogUtils.d(TAG, "" + uMatrixLocation);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        LogUtils.d(TAG, "" + aPositionLocation);

        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        LogUtils.d(TAG, "" + uColorLocation);
    }

    public void setUniforms(float[] matrix, float r, float g, float b){
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

}
