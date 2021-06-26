package com.example.openglpractice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.openglpractice.objects.Mallet;
import com.example.openglpractice.objects.Puck;
import com.example.openglpractice.objects.Table;
import com.example.openglpractice.programs.ColorShaderProgram;
import com.example.openglpractice.programs.TextureShaderProgram;
import com.example.openglpractice.util.Geometry;
import com.example.openglpractice.util.LogUtils;
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
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
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

    /**
     * 逆视图矩阵和投影矩阵
     */
    private final float[] invertedViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

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
        mallet = new Mallet(0.08f, 0.15f, 32); // 生成数据
        puck = new Puck(0.06f, 0.02f, 32);

        colorShaderProgram = new ColorShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);

        texture = TextureUtils.loadTexture(
                context, R.drawable.air_hockey_surface
        );

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2, 0.4f);
    }


    /**
     * 设置视口
     * 生成透视矩阵
     * 生成视图矩阵
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口尺寸，通知OpenGL用于渲染的surface大小
        glViewport(0, 0, width, height);

        // 创建一个从-1到-10的视锥体
        MatrixUtils.perspectiveM(projectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);
    }


    private float eyeAngle = 0f;
    private float radius = 3f;

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空渲染表面
        glClear(GL_COLOR_BUFFER_BIT);

        eyeAngle += 0.1f;
        float eyeX = 3f * (float) Math.cos(eyeAngle);
        float eyeZ = 3f * (float) Math.sin(eyeAngle);

        setLookAtM(
                viewMatrix, 0, // 结果矩阵
                0, 1.2f, 2.2f, // 眼睛位置
                0f, 0f, 0f, // 眼睛看的位置
                0f, 1f, 0f // 头顶方向
        );

        // 将透视矩阵与视图矩阵相乘
        multiplyMM(projectionViewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // 获得你矩阵
        invertM(invertedViewProjectionMatrix, 0, projectionViewMatrix, 0);

        // 生成桌子的模型矩阵，绘制桌子
        positionTableInScene();
        textureShaderProgram.useProgram(); // 使用当前着色器
        textureShaderProgram.setUniforms(projectionViewModelMatrix, texture); // 设置矩阵与纹理数据
        table.bindData(textureShaderProgram); // 绑定顶点属性
        table.draw(); // 绘制

        // 生成木槌的模型矩阵，绘制木槌
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionViewModelMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
        );
        LogUtils.d(TAG, "blue X: " + blueMalletPosition.x
            + " blue Y: " + blueMalletPosition.y
            + " blue Z: " + blueMalletPosition.z);
        colorShaderProgram.setUniforms(projectionViewModelMatrix, 0f, 0f, 1f);
        mallet.draw();

        // 生成冰球的模型矩阵，绘制冰球
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
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(projectionViewModelMatrix, 0,
                projectionViewMatrix, 0, modelMatrix, 0);
    }

    /**
     * 响应按下事件
     *
     * @param normalizedX
     * @param normalizedY
     */
    public void handleTouchPress(float normalizedX, float normalizedY) {
        // 获取三位世界射线
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        // 获取木槌的包围球
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(
                new Geometry.Point(
                    blueMalletPosition.x,
                    blueMalletPosition.y,
                    blueMalletPosition.z
                ),
                mallet.height / 2
        );
        // 检测是否按到了木槌
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    /**
     * 响应移动事件
     *  只有被按压到时才会移动
     * @param normalizedX
     * @param normalizedY
     */
    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Geometry.Plane plane = new Geometry.Plane(
                    new Geometry.Point(0, 0, 0),
                    new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            blueMalletPosition =
                    new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
        }
    }

    /**
     * 将触摸的二维坐标转换成三位空间的射线
     * 原理：将被触摸的归一化点通过逆矩阵，还原成视锥体上远近平面的两个点，以此建立一条世界坐标体系下的射线
     *
     * @param normalizedX
     * @param normalizedY
     * @return
     */
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1}; // 归一化坐标体系下的近平面点
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1}; // 归一化坐标体系下的远平面点

        final float[] nearPointWorld = new float[4]; // 世界坐标系下的近平面点
        final float[] farPointWorld = new float[4];

        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0
        );
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0
        );

        //撤销透视除法的影响，直接使用逆矩阵的反转w即可
        divideByW(nearPointNdc);
        divideByW(farPointNdc);

        // 通过顶点构造射线
        Geometry.Point nearPointRay =
                new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay =
                new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        LogUtils.d(TAG, "nearPointRay: " + nearPointRay
            + "\nfarPointRay: " + farPointRay);

        return new Geometry.Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    /**
     * 将vector向量除以w，撤销透视除法影响
     *
     * @param vector
     */
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}
