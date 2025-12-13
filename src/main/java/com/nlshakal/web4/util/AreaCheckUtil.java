package com.nlshakal.web4.util;

public final class AreaCheckUtil {

    private AreaCheckUtil() {}

    public static boolean checkHit(double x, double y, double r) {
        return checkPoint(x, y, r);
    }

    public static boolean checkPoint(Double x, Double y, Double r) {
        if (x == null || y == null || r == null || r <= 0) {
            return false;
        }

        return checkRectangle(x, y, r) ||
                checkTriangle(x, y, r) ||
                checkCircle(x, y, r);
    }

    /**
     * Проверяет попадание в прямоугольник во 2-м квадранте.
     * Область: x ∈ [-r, 0], y ∈ [0, r/2]
     */
    private static boolean checkRectangle(double x, double y, double r) {
        return x <= 0 && x >= -r && y >= 0 && y <= r/2;
    }

    /**
     * Проверяет попадание в треугольник в 3-м квадранте.
     * Область ограничена линией y = -x/2 - r/2
     */
    private static boolean checkTriangle(double x, double y, double r) {
        return x <= 0 && x >= -r && y <= 0 && y >= -x/2 - r/2;
    }

    /**
     * Проверяет попадание в четверть круга в 1-м квадранте.
     * Радиус круга равен r/2
     */
    private static boolean checkCircle(double x, double y, double r) {
        return x >= 0 && y >= 0 && (x * x + y * y) <= (r/2 * r/2);
    }
}

