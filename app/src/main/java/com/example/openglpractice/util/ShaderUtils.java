package com.example.openglpractice.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderUtils {

    private static final String TAG = "ShaderUtils";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 创建一个新的着色器对象，并检查该创建是否成功
     *
     * @param type,       调用OpenGL编译接口时传入的glsl文件类型
     * @param shaderCode, 文件源码
     * @return 如果编译成功，则返回着色器对象Id；否则，返回0表示编译失败
     */
    private static int compileShader(int type, String shaderCode) {
        // 用glCreateShader创建指定类型的着色器对象，并将这个对象的Id存入shaderObjectId
        final int shaderObjectId = glCreateShader(type);

        // 如果OpenGL返回的是0，则表示该对象创建失败
        if (shaderObjectId == 0) {
            return 0;
        }

        // 把着色器代码上传到着色器对象shaderObjectId
        glShaderSource(shaderObjectId, shaderCode);

        // 编译shaderObjectId的源代码
        glCompileShader(shaderObjectId);

        // 检查OpenGL是否成功的编译着色器
        final int[] compileStatus = new int[1];
        // 查询编译状态
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        // 查看编译是否成功
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            LogUtils.e(TAG, "Compilation of shader failed: " + glGetShaderInfoLog(shaderObjectId) + "\n" + shaderCode);

            glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }


    /**
     * 使用顶点着色器和片段着色器链接成一个OpenGL程序
     *
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 新建OpenGL程序
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            LogUtils.e(TAG, "Could not create new program");
            return 0;
        }

        // 附上着色器
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        // 链接着色器
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        // 验证链接状态
        if (linkStatus[0] == 0) {
            LogUtils.e(TAG, "Linking of program failed: " + glGetProgramInfoLog(programObjectId));
            glDeleteShader(programObjectId);
            return 0;
        }

        return programObjectId;
    }

    /**
     * 验证程序对于当前OpenGL是否有效
     *
     * @param programObjectId
     * @return
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validStatus, 0);
        LogUtils.d(TAG, "Results of validating program: " + validStatus[0]
                + "\nLog: " + glGetProgramInfoLog(programObjectId));

        return validStatus[0] != 0;
    }


    /**
     * 编译vertexShaderSource 和 fragmentShaderSource定义的着色器
     * @param vertexShaderSource
     * @param fragmentShaderSource
     * @return
     */
    public static int buildProgram(
            String vertexShaderSource,
            String fragmentShaderSource) {
        int program;

        Log.d(TAG, "buildProgram: vertex: " + vertexShaderSource +
                "\n--------------------------------\n" +
                "fragment: " + fragmentShaderSource);

        // 编译
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        // 链接
        program = linkProgram(vertexShader, fragmentShader);

        validateProgram(program);

        return program;
    }

}
