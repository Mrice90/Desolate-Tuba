package com.infiniteconquest.core;

public record BoardPosition(int x, int y) {
    public static final int WIDTH = 4;
    public static final int HEIGHT = 3;

    public BoardPosition {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            throw new IllegalArgumentException("Position outside 4x3 battlefield: " + x + "," + y);
        }
    }

    public int manhattanDistance(BoardPosition other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public boolean adjacentTo(BoardPosition other) {
        return manhattanDistance(other) == 1;
    }
}
