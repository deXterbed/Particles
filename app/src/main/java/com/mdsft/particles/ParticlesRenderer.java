package com.mdsft.particles;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import com.mdsft.particles.objects.*;
import com.mdsft.particles.programs.*;
import com.mdsft.particles.util.Geometry.*;
import com.mdsft.particles.util.MatrixHelper;
import com.mdsft.particles.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

public class ParticlesRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkybox = new float[16];
    private final float[] projectionMatrix = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int particleTexture;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;

    private HeightmapShaderProgram heightmapProgram;
    private Heightmap heightmap;

    private float xRotation, yRotation;

    private final Vector vectorToLight = new Vector(0.30f, 0.35f, -0.89f).normalize();

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(20000);
        globalStartTime = System.nanoTime();
        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context,
            new int[] {
                R.drawable.night_left, R.drawable.night_right,
                R.drawable.night_bottom, R.drawable.night_top,
                R.drawable.night_front, R.drawable.night_back
            });

        heightmapProgram = new HeightmapShaderProgram(context);
        heightmap = new Heightmap(BitmapFactory.decodeResource(context.getResources(),
            R.drawable.heightmap));

        final Vector particleDirection = new Vector(0f, 0.5f, 0f);

        final float angleVarianceInDegrees = 15f;
        final float speedVariance = 1f;

        redParticleShooter = new ParticleShooter( new Point(-0.8f, 0f, 0f), particleDirection,
            Color.rgb(255, 50, 5), angleVarianceInDegrees, speedVariance);

        greenParticleShooter = new ParticleShooter( new Point(0f, 0f, 0f), particleDirection,
            Color.rgb(255, 192, 203), angleVarianceInDegrees, speedVariance);

        blueParticleShooter = new ParticleShooter( new Point(0.8f, 0f, 0f), particleDirection,
            Color.rgb(5, 50, 255), angleVarianceInDegrees, speedVariance);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 100f);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    private void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkybox();

        glDepthFunc(GL_LEQUAL);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForParticles();

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();
        heightmapProgram.setUniforms(modelViewProjectionMatrix, vectorToLight);
        heightmap.bindData(heightmapProgram);
        heightmap.draw();
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
        updateViewMatrices();
    }

    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);

        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0, -1.5f, -5f);
    }

    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void updateMvpMatrixForParticles() {
        translateM(modelMatrix, 0, 0, -1.5f, -5f);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }
}
