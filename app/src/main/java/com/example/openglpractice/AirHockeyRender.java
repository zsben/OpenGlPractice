package com.example.openglpractice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.openglpractice.objects.Mallet;
import com.example.openglpractice.objects.Table;
import com.example.openglpractice.programs.ColorShaderProgram;
import com.example.openglpractice.programs.TextureShaderProgram;
import com.example.openglpractice.util.MatrixUtils;
import com.example.openglpractice.util.ShaderUtils;
import com.example.openglpractice.util.TextResourceUtil;
import com.example.openglpractice.util.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final String TAG = "AirHockeyRender";

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();

        colorShaderProgram = new ColorShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);

        texture = TextureUtils.loadTexture(
                context, R.drawable.air_hockey_surface
        );
    }


    /**
     * orthoM(
     * float[] m, 目标数组，用来存储正交矩阵
     * int mOffset, 目标数组偏移量
     * float left, minX
     * float right, maxX
     * float bottom, minY
     * float top, maxY
     * float near, minZ
     * float far, maxZ
     * )
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口尺寸，通知OpenGL用于渲染的surface大小
        glViewport(0, 0, width, height);

        // 创建一个从-1到-10的视锥体
        MatrixUtils.perspectiveM(projectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);

        // 设置单位矩阵
        setIdentityM(modelMatrix, 0);
        // 改为延z轴平移-2变换矩阵
        translateM(modelMatrix, 0, 0f, 0f, -5f);
        // 设置绕x轴旋转
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        // 将透视矩阵 * 平移矩阵，最后结果存在projectionMatrix
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
        for (int i = 0; i < 16; i++) {
            Log.d(TAG, "useMatrix: " + i + ": +" + projectionMatrix[i]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空渲染表面
        glClear(GL_COLOR_BUFFER_BIT);

        // 绘制桌子
        textureShaderProgram.useProgram(); // 使用当前着色器
        textureShaderProgram.setUniforms(projectionMatrix, texture); // 设置矩阵与纹理数据
        table.bindData(textureShaderProgram); // 绑定顶点属性
        table.draw(); // 绘制

        // 绘制木槌
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }

}
