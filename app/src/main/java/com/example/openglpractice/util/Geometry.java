package com.example.openglpractice.util;

public class Geometry {

    /**
     * 点坐标
     */
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

        public Point translate(Vector vector) {
            return new Point(
                    x + vector.x,
                    y + vector.y,
                    z + vector.z);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    /**
     * 圆（圆心点，半径）
     */
    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    /**
     * 圆柱（圆心点，半径，高度）
     */
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    /**
     * 射线：顶点+向量
     */
    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    /**
     * 向量
     */
    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * 向量的模
         *
         * @return
         */
        public float length() {
            return (float) Math.sqrt(
                    x * x + y * y + z * z
            );
        }

        /**
         * 两向量叉乘
         *
         * @param other
         * @return
         */
        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x)
            );
        }

        /**
         * 两向量点击
         *
         * @param other
         * @return
         */
        public float dotProduce(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        /**
         * 缩放向量
         * @param f
         * @return
         */
        public Vector scale(float f) {
            return new Vector(
                    x * f,
                    y * f,
                    z * f
            );
        }

        public void print() {
            LogUtils.d("Vector", "Vector x: " + x + ",y:" + y + ",z:" + z);
        }
    }

    /**
     * 球体
     */
    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    /**
     * 平面：通过平面上任意一点+法向量构造
     */
    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    /**
     * 两点之间的向量
     *
     * @param from
     * @param to
     * @return
     */
    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
        );
    }

    /**
     * 相交测试
     */
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /**
     * 点线距离
     */
    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        float areaOfTriangleTimesTow = p1ToPoint.crossProduct(p2ToPoint).length(); // len1 * len2 * sin(alpha)
        float lengthOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTow / lengthOfBase; // 面积 * 2 / 底部长度
        return distanceFromPointToRay;
    }

    /**
     * 射线（直线）与平面交点
     *
     * @param ray
     * @param plane
     * @return
     */
    public static Point intersectionPoint(Ray ray, Plane plane) {
        // 向量1 = 射线顶点到平面顶点
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);

        // 缩放因子 = 向量1与ray在法向量上投影的比值
        float scaleFactor = rayToPlaneVector.dotProduce(plane.normal)
                / ray.vector.dotProduce(plane.normal);

        // 将ray的向量缩放到与plane刚好相交
        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }

}
