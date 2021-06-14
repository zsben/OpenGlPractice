package com.example.openglpractice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.openglpractice.util.ShaderUtils;
import com.example.openglpractice.util.TextResourceUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
// 对于每一个单独定义过的顶点，顶点着色器都会被调用一次；
// 当它被调用的时候，它会在a_Position属性里接收当前定点的位置, 这个属性被定义成vec4类型
// vec4包含4个分量：x,y,z,w，默认x,y,z=0，w=1
// attribute就是把java文件中属性放进着色器的手段

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final String TAG = "AirHockeyRender";

    // 定制属性分量个数
    private static final int POSITION_COMPONENT_COUNT = 2;
    // 每个float的字节数
    private static final int BYTES_PER_FLOAT = 4;
    // 定点属性数组， 使用三角形卷曲顺序（逆时针）
    float[] tableVerticesWithTriangles = {
            // Triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            // Triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            // Line 1
            -0.5f, 0f,
            0.5f, 0f,

            // Mallets
            0f, -0.5f,
            0f, 0.5f
    };

    // 为片段着色器中的uniform创建一个常量
    private static final String U_COLOR = "u_Color";
    // 用来容纳它在OpenGL程序对象中位置的变量，程序链接重构后查询该位置
    // 一个uniform的位置在一个程序对象中是唯一的
    private int uColorLocation;

    // 定义属性的常量，获取属性的位置
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    // 用于在本地内存中存储定点属性数组，传给OpenGL使用
    private final FloatBuffer vertexData;

    private final Context context;

    // OpenGL程序的Id
    private int program;

    public AirHockeyRender(Context context) {
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 清空屏幕颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // 读取两个着色器的源代码
        String vertexShaderSource = TextResourceUtil
                .readTextFileFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceUtil
                .readTextFileFromResource(context, R.raw.fragment);

        // 编译两个着色器
        int vertexShader = ShaderUtils.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderUtils.compileFragmentShader(fragmentShaderSource);

        // 链接程序
        program = ShaderUtils.linkProgram(vertexShader, fragmentShader);

        // 验证程序是否有效
        ShaderUtils.validateProgram(program);

        // 告诉OpenGL在绘制任何东西到屏幕上时要使用这里定义的程序
        glUseProgram(program);

        // 获取u_Color的位置
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        // 获取a_Position的属性位置
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // 关联属性与顶点数据的数组
        attachVertexData(aPositionLocation, vertexData);
    }


    /**
     * 通知OpenGL在vertexData读取positionLocation的数据
     *
     * @param positionLocation
     * @param vertexData
     */
    void attachVertexData(int positionLocation, FloatBuffer vertexData) {
        vertexData.position(0);
        /**
         * public static void glVertexAttribPointer(
         *         int index, 属性位置，即通知OpenGL读取数据到哪里
         *         int size, 每个属性的数据的计数，即有多少分量与每个顶点关联；
         *                   这里为每个顶点传了两个分量，着色器中的vec4 a_Position剩下两个分量用默认值
         *         int type, 数据类型
         *         boolean normalized, 使用整型时才有用
         *         int stride, 只有档一个数组存储多于一个属性时，它才有意义
         *         java.nio.Buffer ptr 告诉OpenGL去哪里读取数据
         *     )
         */
        glVertexAttribPointer(positionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData);
        // 使能顶点数组
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口尺寸，通知OpenGL用于渲染的surface大小
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空屏幕，然后调用glClearColor定义的颜色填充屏幕
        glClear(GL_COLOR_BUFFER_BIT);

        drawTriangles();
        drawLines();
        drawMallet();
    }

    /**
     * 画三角形
     */
    void drawTriangles() {
        // 更新着色器中u_Color的值
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);

        /**
         * public static native void glDrawArrays(
         *         int mode, 绘制类型
         *         int first, 告诉OpenGL从顶点数组的某个位置开始读顶点
         *         int count, 读入多少数据
         *     );
         */
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    /**
     * 画线
     */
    void drawLines() {
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
    }

    /**
     * 画点
     */
    void drawMallet() {
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }

}
