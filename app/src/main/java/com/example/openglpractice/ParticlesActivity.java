package com.example.openglpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ParticlesActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false; // renderer是否被设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // 使用GLSurfaceView初始化OpenGl
        glSurfaceView = new GLSurfaceView(this);

        // 检测系统是否支持OpenGL ES 2.0
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        final ParticlesRenderer particlesRenderer = new ParticlesRenderer(this);


        if (supportEs2) {
            // 配置rendering surface
            // request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Assign renderer. 设置renderer

            glSurfaceView.setRenderer(particlesRenderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;
                        previousX = event.getX();
                        previousY = event.getY();

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRenderer.handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
        );

        // 把glSurfaceView添加到activity中
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}