package com.example.openglpractice.programs;

import android.content.Context;

import com.example.openglpractice.R;

public class SkyboxShaderProgram {

    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;

    public SkyboxShaderProgram(Context context) {
        super(context, R.raw.skybox_vertex_shader,
                R.raw.skybox_fragment_shader);


    }

}
