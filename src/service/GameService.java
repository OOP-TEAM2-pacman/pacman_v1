package service;

import constant.GameConstants;
import model.Direction;
import model.ItemType;
import model.Map;
import model.Position;
import java.io.IOException;
import java.util.List;

public class GameService {
    private final MapService mapService;
    private final EnemyService enemyService;
    private final ItemService itemService;
    private final CoinService coinService;
    private final GameStateManager gameState;

    private String playerName;
    private Position playerPosition;
    private Map gameMap;
    private int stage;
    private int moveCount; // 이동 횟수 추가

    public GameService() {
        this.mapService = new MapService();
        this.enemyService = new EnemyService();
        this.itemService = new ItemService();
        this.coinService = new CoinService();
        this.gameState = new GameStateManager();
        this.playerName = "Player";
        this.moveCount = 0; // 이동 횟수 초기화
    }

    public void initializeGame() throws IOException {
        stage = 1;
        moveCount = 0; // 이동 횟수 초기화
        gameState.resetState();
        initializeStage();
    }

    private void initializeStage() throws IOException {
        gameMap = new Map(mapService.readMap(stage));
        initializePlayerPosition();

        coinService.initializeCoins(gameMap, playerPosition, stage);
        enemyService.initializeEnemies(gameMap, playerPosition, stage);
        itemService.initializeItems(gameMap, playerPosition,
                enemyService.getEnemies(),
                coinService.getCoins(), stage);
    }

    private void initializePlayerPosition() {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Position pos = new Position(x, y);
                if (gameMap.isEmpty(pos)) {
                    playerPosition = pos;
                    return;
                }
            }
        }
        throw new IllegalStateException("맵에 플레이어가 배치될 공간이 없습니다.");
    }

    public void movePlayer(Direction direction) {
        if (gameState.isGameOver() || gameState.isGameClear()) return;

        Position nextPosition = playerPosition.getNextPosition(direction);
        if (gameMap.isWithinBounds(nextPosition) && gameMap.isEmpty(nextPosition)) {
            playerPosition = nextPosition;
            moveCount++; // 이동 횟수 증가
            updateGameState();
        }
    }

    private void updateGameState() {
        if (coinService.collectCoin(playerPosition)) {
            gameState.handleCoinCollection(gameState.getBoostTime() > 0);
        }

        ItemType collectedItem = itemService.collectItem(playerPosition);
        if (collectedItem != null) {
            gameState.handleItemCollection(collectedItem);
        }

        if (gameState.getFreezeTime() <= 0) {
            enemyService.moveEnemies(gameMap, playerPosition);
        }

        if (enemyService.checkCollision(playerPosition)) {
            gameState.handleCollision();
        }

        if (coinService.areAllCoinsCollected()) {
            handleStageCompletion();
        }

        gameState.updateTimers();
    }

    private void handleStageCompletion() {
        if (stage < GameConstants.MAX_STAGES) {
            stage++;
            try {
                initializeStage();
            } catch (IOException e) {
                e.printStackTrace();
                gameState.setGameOver(true);
            }
        } else {
            gameState.setGameClear(true);
        }
    }

    // 최종 점수 계산
    public int calculateFinalScore() {
        int baseScore = gameState.getScore(); // 코인 점수
        int stageBonus = stage * 1000; // 스테이지 보너스
        int moveDeduction = moveCount * 5; // 이동 횟수에 따른 감점

        return baseScore + stageBonus - moveDeduction;
    }

    // Getters
    public char[][] getMapData() { return gameMap.getMapData(); }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public Position getPlayerPosition() { return playerPosition; }
    public int getScore() { return gameState.getScore(); }
    public int getStage() { return stage; }
    public int getShieldCount() { return gameState.getShieldCount(); }
    public int getTimeCount() { return gameState.getFreezeTime(); }
    public int getBoostTime() { return gameState.getBoostTime(); }
    public int getMoveCount() { return moveCount; } // 이동 횟수 getter 추가
    public boolean isGameOver() { return gameState.isGameOver(); }
    public boolean isGameClear() { return gameState.isGameClear(); }
    public int getItemCount() {
        return itemService.getItems().size();
    }

    public int getCoinCount() {
        return coinService.getCoins().size();
    }

    // Delegating methods for view
    public List<Position> getEnemies() { return enemyService.getEnemies(); }
    public List<Position> getCoins() { return coinService.getCoins(); }
    public List<Position> getItems() { return itemService.getItems(); }
    public List<ItemType> getItemTypes() { return itemService.getItemTypes(); }
}