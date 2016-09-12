package com.mdsft.particles.programs;

import android.content.Context;

import com.mdsft.particles.R;
import com.mdsft.particles.util.Geometry.*;

import static android.opengl.GLES20.*;

public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uVectorToLightLocation;

    private final int uTextureUnitLocation1;
    private final int uTextureUnitLocation2;

    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);

        uTextureUnitLocation1 = glGetUniformLocation(program, U_TEXTURE_UNIT_1);
        uTextureUnitLocation2 = glGetUniformLocation(program, U_TEXTURE_UNIT_2);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, Vector vectorToLight,
                            int grassTextureId, int stoneTextureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform3f(uVectorToLightLocation,
            vectorToLight.x, vectorToLight.y, vectorToLight.z);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, grassTextureId);
        glUniform1i(uTextureUnitLocation1, 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, stoneTextureId);
        glUniform1i(uTextureUnitLocation2, 1);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
