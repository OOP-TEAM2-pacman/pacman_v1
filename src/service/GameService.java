// GameModel.java
package model;

import constant.GameConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    // Game state
    private String playerName;
    private Position playerPosition;
    private List<Position> enemies;
    private List<Position> coins;
    private List<Position> items;
    private List<ItemType> itemTypes;
    private int score;
    private int stage;
    private int shieldCount;
    private int timeCount;
    private boolean isGameOver;
    private boolean isGameClear;
    private final Random random;
    private long startTime; // 게임 시작 시간 // 추가
    private long elapsedTime; // 경과 시간 // 추가
    private int boostTime; // 추가
    //아이템 사용횟수
    private int totalShield = 0;
    private int totalFrozen = 0;

    public void startTimer() {
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
    } // 추가

    public void stopTimer() {
        elapsedTime = System.currentTimeMillis() - startTime;
    } // 추가

    public int getElapsedTime() {
        if (isGameOver || isGameClear) {
            return (int) (elapsedTime / 1000); // 게임 오버, 클리어 시 경과 시간 고정
        }
        return (int) ((System.currentTimeMillis() - startTime) / 1000); // 진행 중
    } // 추가
    public GameModel() {
        this.playerName = "Player";
        this.enemies = new ArrayList<>();
        this.coins = new ArrayList<>();
        this.items = new ArrayList<>();
        this.itemTypes = new ArrayList<>();
        this.random = new Random();
        initializeGame();
    }

    public void initializeGame() {
        playerPosition = new Position(GameConstants.BOARD_SIZE / 2, GameConstants.BOARD_SIZE / 2);
        enemies.clear();
        coins.clear();
        items.clear();
        itemTypes.clear();
        score = 0;
        stage = 1;
        shieldCount = 0;
        timeCount = 0;
        isGameOver = false;
        isGameClear = false;
        boostTime = 0; // 추가
        initializeStage();
    }

    private void initializeStage() {
        initializeCoins();
        initializeEnemies();
        initializeItems();
    }

    private void initializeCoins() {
        int numCoins = stage * 10;
        for (int i = 0; i < numCoins; i++) {
            Position coin;
            do {
                coin = new Position(
                        random.nextInt(GameConstants.BOARD_SIZE),
                        random.nextInt(GameConstants.BOARD_SIZE)
                );
            } while (coins.contains(coin) || coin.equals(playerPosition));
            coins.add(coin);
        }
    }

    private void initializeEnemies() {
        int numEnemies = stage + 4;
        enemies.clear();  // 이전 스테이지의 적들을 모두 제거

        for (int i = 0; i < numEnemies; i++) {
            Position enemy;
            do {
                enemy = new Position(
                        random.nextInt(GameConstants.BOARD_SIZE),
                        random.nextInt(GameConstants.BOARD_SIZE)
                );
            } while (enemies.contains(enemy) ||
                    enemy.equals(playerPosition) ||
                    isNearPlayer(enemy, 2));
            enemies.add(enemy);
        }
    }

    private void initializeItems() {
        int numItems = stage + 2;
        items.clear();
        itemTypes.clear();

        for (int i = 0; i < numItems; i++) {
            Position item;
            do {
                item = new Position(
                        random.nextInt(GameConstants.BOARD_SIZE),
                        random.nextInt(GameConstants.BOARD_SIZE)
                );
            } while (coins.contains(item) ||
                    enemies.contains(item) ||
                    item.equals(playerPosition));

            items.add(item);
            int randomValue = random.nextInt(10); // 0~9 사이의 값
            if (randomValue < 4) { // 40% 확률로 SHIELD
                itemTypes.add(ItemType.SHIELD);
            } else if (randomValue < 8) { // 40% 확률로 FREEZE
                itemTypes.add(ItemType.FREEZE);
            } else { // 20% 확률로 BOOST
                itemTypes.add(ItemType.BOOST);
            } // 추가
        }
    }

    private boolean isNearPlayer(Position enemy, int distance) {
        return Math.abs(enemy.getX() - playerPosition.getX()) <= distance &&
                Math.abs(enemy.getY() - playerPosition.getY()) <= distance;
    }

    public void movePlayer(Direction direction) {
        if (isGameOver || isGameClear) {
            if (isGameOver) {
                resetGame();
                return;
            }
            return;
        }

        playerPosition.move(direction, GameConstants.BOARD_SIZE);
        updateGameState();
    }

    private void updateGameState() {
        checkCoinCollection();
        checkItemCollection();
        updateEnemies();
        checkCollisions();
        checkStageCompletion();
    }

    private void checkCoinCollection() {
        coins.removeIf(coin -> {
            if (coin.equals(playerPosition)) {
                if (boostTime > 0) {
                    score += GameConstants.COIN_SCORE * 2; // BOOST 효과 적용
                    System.out.println("BOOST applied! Double points."); // 추가
                } else {
                    score += GameConstants.COIN_SCORE;
                }
                return true;
            }
            return false;
        });
    }

    private void checkItemCollection() {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).equals(playerPosition)) {
                ItemType type = itemTypes.get(i);
                if (type == ItemType.SHIELD) {
                    shieldCount++;
                    System.out.println("Shield acquired! Count: " + shieldCount);
                } else if (type == ItemType.FREEZE) {
                    timeCount = GameConstants.FREEZE_TIME;
                    System.out.println("Freeze activated! Duration: " + timeCount);
                } else if (type == ItemType.BOOST) {
                    boostTime = GameConstants.BOOST_DURATION; // 활성화
                    System.out.println("BOOST activated! Duration: " + boostTime);
                } // 추가
                items.remove(i);
                itemTypes.remove(i);
            }
        }
    }

    private void checkStageCompletion() {
        if (coins.isEmpty()) {
            if (stage < GameConstants.MAX_STAGES) {
                stage++;
                initializeStage();
            } else {
                isGameClear = true;
                stopTimer(); // 추가
            }
        }
    }

    private void updateEnemies() {
        if (timeCount > 0) {
            System.out.println("Enemies frozen. Remaining time: " + timeCount);
            timeCount--;
        }
        if (boostTime > 0) {
            System.out.println("BOOST active. Remaining time: " + boostTime);
            boostTime--;
        } // 추가
        if (timeCount == 0) {
            moveEnemies();
        } // 추가
    }

    private void moveEnemies() {
        for (Position enemy : enemies) {
            Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];
            enemy.move(randomDirection, GameConstants.BOARD_SIZE);
        }
    }

    private void checkCollisions() {
        for (Position enemy : enemies) {
            if (enemy.equals(playerPosition)) {
                handleCollision();
                break;
            }
        }
    }

    private void handleCollision() {
        if (shieldCount > 0) {
            shieldCount--;
            score -= 10; // 추가
            System.out.println("Shield used! Remaining: " + shieldCount);
        } else {
            isGameOver = true;
            stopTimer(); // 추가
            System.out.println("Game Over! Score: " + score);
        }
    }

    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public Position getPlayerPosition() { return playerPosition; }
    public List<Position> getEnemies() { return new ArrayList<>(enemies); }
    public List<Position> getCoins() { return new ArrayList<>(coins); }
    public List<Position> getItems() { return new ArrayList<>(items); }
    public List<ItemType> getItemTypes() { return new ArrayList<>(itemTypes); }
    public int getScore() { return score; }
    public int getStage() { return stage; }
    public int getShieldCount() { return shieldCount; }
    public int getTimeCount() { return timeCount; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isGameClear() { return isGameClear; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getBoostTime() { return  boostTime;} // 추가
    public void resetGame() { initializeGame(); }
}