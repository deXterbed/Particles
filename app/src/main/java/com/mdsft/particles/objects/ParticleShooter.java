package com.mdsft.particles.objects;

import com.mdsft.particles.util.Geometry.*;

import java.util.Random;

import static android.opengl.Matrix.*;

public class ParticleShooter {
    private final Point position;
    private final int color;

    private final float angleVariance;
    private final float speedVariance;

    private final Random random = new Random();

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];


    public ParticleShooter(Point position, Vector direction, int color,
                           float angleVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.color = color;

        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;

        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;

    }

    public void addParticles(ParticleSystem particleSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
            setRotateEulerM(rotationMatrix, 0,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance);

            multiplyMV(
                resultVector, 0,
                rotationMatrix, 0,
                directionVector, 0);

            float speedAdjustment = 1f + random.nextFloat() * speedVariance;

            Vector thisDirection = new Vector( resultVector[0] * speedAdjustment,
                resultVector[1] * speedAdjustment, resultVector[2] * speedAdjustment);

            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}
