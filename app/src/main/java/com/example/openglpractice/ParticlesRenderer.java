package com.example.openglpractice;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;


import com.example.openglpractice.objects.ParticleShooter;
import com.example.openglpractice.objects.ParticleSystem;
import com.example.openglpractice.objects.SkyBox;
import com.example.openglpractice.programs.ParticleShaderProgram;
import com.example.openglpractice.programs.SkyBoxShaderProgram;
import com.example.openglpractice.util.Geometry.*;
import com.example.openglpractice.util.MatrixHelper;
import com.example.openglpractice.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;


public class ParticlesRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 1f;

    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter blueParticleShooter;
    private ParticleShooter greenParticleShooter;
    private long globalStartTime;

    private int particleTexture;

    private SkyBoxShaderProgram skyBoxShaderProgram;
    private SkyBox skyBox;
    private int skyBoxTexture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final Vector particleDirection = new Vector(0f, 0.5f, 0f);

        redParticleShooter = new ParticleShooter(
                new Point(-0.8f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance

        );

        greenParticleShooter = new ParticleShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance
        );

        blueParticleShooter = new ParticleShooter(
                new Point(0.8f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance
        );

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        skyBoxShaderProgram = new SkyBoxShaderProgram(context);
        skyBox = new SkyBox();
        skyBoxTexture = TextureHelper.loadCubeMap(context,
                new int[] {
                        R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top,
                        R.drawable.front, R.drawable.back
                });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        drawSkyBox();
        drawParticles();
    }

    /**
     * 绘制天空盒子
     */
    private void drawSkyBox(){
        setIdentityM(viewMatrix, 0);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0
                , viewMatrix, 0);

        skyBoxShaderProgram.useProgram();
        skyBoxShaderProgram.setUniforms(viewProjectionMatrix, skyBoxTexture);
        skyBox.bindData(skyBoxShaderProgram);
        skyBox.draw();
    }

    /**
     * 绘制粒子效果
     */
    private void drawParticles(){

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0
                , viewMatrix, 0);

        particleProgram.useProgram();
        setIdentityM(viewProjectionMatrix, 0);
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
    }
}
