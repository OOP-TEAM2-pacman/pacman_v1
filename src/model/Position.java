package model;

import java.util.Objects;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void move(Direction direction, int width, int height) {
        switch (direction) {
            case UP -> y = Math.max(0, y - 1);
            case DOWN -> y = Math.min(height - 1, y + 1);
            case LEFT -> x = Math.max(0, x - 1);
            case RIGHT -> x = Math.min(width - 1, x + 1);
        }
    }

    public Position getNextPosition(Direction direction) {
        int newX = x;
        int newY = y;

        switch (direction) {
            case UP -> newY--;
            case DOWN -> newY++;
            case LEFT -> newX--;
            case RIGHT -> newX++;
        }

        return new Position(newX, newY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}