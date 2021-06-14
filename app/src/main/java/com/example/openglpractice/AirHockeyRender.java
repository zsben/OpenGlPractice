package com.example.openglpractice;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    // 定点分量个数
    private static final int POSITION_COMPONENT_COUNT = 2;
    // 每个float的字节数
    private static final int BYTES_PER_FLOAT = 4;
    // 定点属性数组， 使用三角形卷曲顺序（逆时针）
    float[] tableVerticesWithTriangles = {
        // Triangle 1
        // Triangle 2
        // Line 1
        // Mallets
    };
    // 用于在本地内存中存储定点属性数组，传给OpenGL使用
    private final FloatBuffer vertexData;

    public AirHockeyRender() {
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 清空屏幕颜色
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
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
    }
}
