package com.example.openglpractice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.openglpractice.objects.Mallet;
import com.example.openglpractice.objects.Puck;
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
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final String TAG = "AirHockeyRender";

    private final Context context;

    /**
     * projection矩阵：投影矩阵
     * View矩阵：视图矩阵
     * Model矩阵：模型矩阵
     * 最终：vertex_clip = projectionMatrix * viewMatrix * modelMatrix * vertex_model
     */
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] projectionViewMatrix = new float[16];
    private final float[] projectionViewModelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRender(Context context) {
        this.context = context;
    }

    /**
     * surface被创建后的工作：
     * 1.实例化两个用于保存顶点属性数据的实体类
     * 2.创建两个着色器程序，分别用于上色和纹理映射
     * 3.加载图片资源到纹理单元上
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        colorShaderProgram = new ColorShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);

        texture = TextureUtils.loadTexture(
                context, R.drawable.air_hockey_surface
        );
    }


    /**
     * surface窗口大小被改变后的工作
     * 1.设置透视矩阵
     * 2.平移，旋转
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口尺寸，通知OpenGL用于渲染的surface大小
        glViewport(0, 0, width, height);

        // 创建一个从-1到-10的视锥体
        MatrixUtils.perspectiveM(projectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);

        setLookAtM(
                viewMatrix, 0, // 结果矩阵
                0f, 1.2f, 2.2f, // 眼睛位置
                0f, 0f, 0f, // 眼睛看的位置
                0f, 1f, 0f // 头顶方向
        );
    }

    /**
     * 绘制每一帧的工作
     * 将纹理与顶点等绘制到OpenGL图元上
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空渲染表面
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(projectionViewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // 绘制桌子
        positionTableInScene();
        textureShaderProgram.useProgram(); // 使用当前着色器
        textureShaderProgram.setUniforms(projectionMatrix, texture); // 设置矩阵与纹理数据
        table.bindData(textureShaderProgram); // 绑定顶点属性
        table.draw(); // 绘制

        // 绘制木槌
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionViewModelMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorShaderProgram.setUniforms(projectionViewModelMatrix, 1f, 0f, 0f);
        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorShaderProgram.setUniforms(projectionViewModelMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();
    }

    /**
     * 生成桌面的model矩阵，需要旋转
     */
    private void positionTableInScene() {
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(projectionViewModelMatrix, 0,
                projectionViewMatrix, 0, modelMatrix, 0);
    }

    /**
     * 生成其他组件的model矩阵,自身带z坐标，只需要平移即可
     */
    private void positionObjectInScene(float x, float y, float z){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(projectionViewModelMatrix, 0,
                projectionViewMatrix, 0, modelMatrix, 0);
    }

}
