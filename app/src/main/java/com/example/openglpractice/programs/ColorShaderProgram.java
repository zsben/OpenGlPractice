package com.example.openglpractice.programs;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.openglpractice.R;
import com.example.openglpractice.util.LogUtils;

public class ColorShaderProgram extends ShaderProgram {

    private final static String TAG = "ColorShaderProgram";

    private final int uMatrixLocation;

    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment);

        LogUtils.d(TAG, "create ColorShaderProgram" + program);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        LogUtils.d(TAG, "" + uMatrixLocation);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        LogUtils.d(TAG, "" + aPositionLocation);

        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        LogUtils.d(TAG, "" + aColorLocation);
    }

    public void setUniforms(float[] matrix){
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
