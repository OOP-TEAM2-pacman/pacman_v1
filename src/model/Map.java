package model;

public class Map {
    private char[][] mapData;

    public Map(char[][] mapData) {
        this.mapData = mapData;
    }

    public char[][] getMapData() {
        return mapData;
    }

    public void setMapData(char[][] mapData) {
        this.mapData = mapData;
    }

    public boolean isWall(int x, int y) {
        return mapData[y][x] == '#';
    }

    public boolean isEmpty(Position position) {
        return position.getX() >= 0 && position.getX() < getWidth() &&
                position.getY() >= 0 && position.getY() < getHeight() &&
                !isWall(position.getX(), position.getY());
    }

    public int getWidth() {
        return mapData[0].length;
    }

    public int getHeight() {
        return mapData.length;
    }

    public boolean isWithinBounds(Position position) {
        return position.getX() >= 0 && position.getX() < getWidth() &&
                position.getY() >= 0 && position.getY() < getHeight();
    }


}
