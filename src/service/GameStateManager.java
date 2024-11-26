package service;

import constant.GameConstants;
import model.ItemType;
import model.Position;

public class GameStateManager {
    private int score;
    private int shieldCount;
    private int freezeTime;
    private int boostTime;
    private boolean isGameOver;
    private boolean isGameClear;
    private long startTime;
    private long elapsedTime;

    public GameStateManager() {
        resetState();
    }

    public void resetState() {
        score = 0;
        shieldCount = 0;
        freezeTime = 0;
        boostTime = 0;
        isGameOver = false;
        isGameClear = false;
        startTime = 0;
        elapsedTime = 0;
    }

    public void handleItemCollection(ItemType type) {
        switch (type) {
            case SHIELD -> shieldCount++;
            case FREEZE -> freezeTime = GameConstants.FREEZE_TIME;
            case BOOST -> boostTime = GameConstants.BOOST_DURATION;
        }
    }

    public void handleCoinCollection(boolean isBoostActive) {
        score += isBoostActive ? GameConstants.COIN_SCORE * 2 : GameConstants.COIN_SCORE;
    }

    public void handleCollision() {
        if (shieldCount > 0) {
            shieldCount--;
        } else {
            isGameOver = true;
        }
    }

    public void updateTimers() {
        if (freezeTime > 0) freezeTime--;
        if (boostTime > 0) boostTime--;
        if (startTime > 0) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        }
    }

    // Getters and setters
    public int getScore() { return score; }
    public int getShieldCount() { return shieldCount; }
    public int getFreezeTime() { return freezeTime; }
    public int getBoostTime() { return boostTime; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public boolean isGameClear() { return isGameClear; }
    public void setGameClear(boolean gameClear) { isGameClear = gameClear; }
    public long getElapsedTime() { return elapsedTime; }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        if (startTime > 0) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        }
    }
}