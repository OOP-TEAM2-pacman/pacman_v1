package service;

import model.Direction;
import model.Map;
import model.Position;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class EnemyService {
    private final Random random;
    private List<Position> enemies;

    public EnemyService() {
        this.random = new Random();
        this.enemies = new ArrayList<>();
    }

    public void initializeEnemies(Map gameMap, Position playerPosition, int stage) {
        enemies.clear();
        int numEnemies = stage + 2;
        for (int i = 0; i < numEnemies; i++) {
            Position enemy;
            do {
                enemy = new Position(
                        random.nextInt(gameMap.getWidth()),
                        random.nextInt(gameMap.getHeight())
                );
            } while (!gameMap.isEmpty(enemy) || enemies.contains(enemy) ||
                    enemy.equals(playerPosition) || isNearPlayer(enemy, playerPosition, 2));
            enemies.add(enemy);
        }
    }

    public void moveEnemies(Map gameMap, Position playerPosition) {
        List<Position> newPositions = new ArrayList<>();

        for (Position enemy : enemies) {
            Direction randomDirection = getRandomValidDirection(enemy, gameMap);
            if (randomDirection != null) {
                Position newPosition = enemy.getNextPosition(randomDirection);
                newPositions.add(newPosition);
            } else {
                newPositions.add(new Position(enemy.getX(), enemy.getY()));
            }
        }

        enemies.clear();
        enemies.addAll(newPositions);
    }

    private Direction getRandomValidDirection(Position enemy, Map gameMap) {
        List<Direction> validDirections = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Position nextPos = enemy.getNextPosition(direction);
            if (gameMap.isWithinBounds(nextPos) && gameMap.isEmpty(nextPos)) {
                validDirections.add(direction);
            }
        }

        if (!validDirections.isEmpty()) {
            return validDirections.get(random.nextInt(validDirections.size()));
        }

        return null;
    }

    private boolean isNearPlayer(Position position, Position playerPosition, int distance) {
        int dx = Math.abs(position.getX() - playerPosition.getX());
        int dy = Math.abs(position.getY() - playerPosition.getY());
        return dx <= distance && dy <= distance;
    }

    public List<Position> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public boolean checkCollision(Position playerPosition) {
        return enemies.stream().anyMatch(enemy -> enemy.equals(playerPosition));
    }
}