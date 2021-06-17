package com.example.openglpractice.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureUtils {

    private static final String TAG = "TextureUtils";

    /**
     * 输入resId，返回加载图像后的OpenGL textureId
     *
     * @param context
     * @param resourceId
     * @return textureId
     * <p>
     * GLES20.glGenTextures(
     * int n, 创建纹理个数
     * int[] textures, 存储纹理id数组，id=0表示创建失败
     * int offset 数组偏移量
     * );
     */
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            LogUtils.e(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        // 使用原始图像数据，而非图像缩放版本
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            LogUtils.e(TAG, "Resource ID " + resourceId + "could not be decoded.");

            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // 告诉OpenGL后面的纹理调用应该应用于这个纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        // 纹理缩小情况使用三线过滤器，在两个等级的Mipmap间再进行线性插值
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大过滤器使用双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 读入bitmap位图数据，复制到当前绑定的纹理对象
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        // 生成所有必要的级别
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 解除此纹理的绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

}
