package com.example.openglpractice.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {

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
        glGenTextures(1, textureObjectIds, 0);

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

        // 将获得的纹理对象绑定到2d纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        // 纹理缩小情况使用三线过滤器，在两个等级的Mipmap间再进行线性插值
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大过滤器使用双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 读入bitmap位图数据，复制到当前绑定的纹理对象
        texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        // 生成所有级别的mipmap
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 解除此纹理的绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    /**
     * 将纹理加载到一个OpenGL立方体贴图上
     * @param context
     * @param cubeResources
     * @return
     */
    public static int loadCubeMap(Context context, int[] cubeResources) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            LogUtils.e(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i=0;i<6;i++) {
            cubeBitmaps[i] =
                    BitmapFactory.decodeResource(context.getResources(),
                            cubeResources[i], options);

            if (cubeBitmaps[i] == null) {
                LogUtils.e(TAG, "Resource ID" + cubeResources[i]
                    + " could not be decoded.");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }

        // 绑定到立方体纹理上
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        // 采用双线性过滤器即可
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 将图像与其对应的立方体贴图的面关联起来
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        // 解除绑定
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        for (Bitmap bitmap: cubeBitmaps) {
            bitmap.recycle();
        }

        return textureObjectIds[0];
    }

}
