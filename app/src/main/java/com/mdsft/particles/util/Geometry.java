package com.mdsft.particles.util;

public class Geometry {
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() { return (float) Math.sqrt(
            x*x + y*y + z*z);
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }

        public Vector crossProduct(Vector other) {
            return new Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x));
        }

        public Vector normalize() {
            return scale(1f / length());
        }

    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
            to.x - from.x,
            to.y - from.y,
            to.z - from.z);
    }
}
