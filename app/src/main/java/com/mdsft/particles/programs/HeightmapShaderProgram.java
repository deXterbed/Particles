package com.mdsft.particles.programs;

import android.content.Context;

import com.mdsft.particles.R;
import com.mdsft.particles.util.Geometry.*;

import static android.opengl.GLES20.*;

public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uVectorToLightLocation;

    private final int aPositionLocation;
    private final int aNormalLocation;

    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    public void setUniforms(float[] matrix, Vector vectorToLight) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform3f(uVectorToLightLocation,
            vectorToLight.x, vectorToLight.y, vectorToLight.z);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }
}
