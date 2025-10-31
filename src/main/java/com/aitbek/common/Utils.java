package com.aitbek.common;

import java.util.Random;

public final class Utils {
    private Utils() {}

    public static int getRandomInRange(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static double getRandomInRange(Random random, double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static void validateProbability(double probability, String paramName) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException(
                    paramName + " must be between 0.0 and 1.0, got: " + probability);
        }
    }

    public static void validateNonNegative(int value, String paramName) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    paramName + " must be non-negative, got: " + value);
        }
    }

    public static void validateVertexIndex(int vertex, int graphSize) {
        if (vertex < 0 || vertex >= graphSize) {
            throw new IllegalArgumentException(
                    "Vertex index must be between 0 and " + (graphSize - 1) + ", got: " + vertex);
        }
    }
}