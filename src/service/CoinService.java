package service;

import model.Map;
import model.Position;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class CoinService {
    private final Random random;
    private List<Position> coins;

    public CoinService() {
        this.random = new Random();
        this.coins = new ArrayList<>();
    }

    public void initializeCoins(Map gameMap, Position playerPosition, int stage) {
        coins.clear();
        int numCoins = stage * 10;
        for (int i = 0; i < numCoins; i++) {
            Position coin;
            do {
                coin = new Position(
                        random.nextInt(gameMap.getWidth()),
                        random.nextInt(gameMap.getHeight())
                );
            } while (!gameMap.isEmpty(coin) || coins.contains(coin) ||
                    coin.equals(playerPosition));
            coins.add(coin);
        }
    }

    public boolean collectCoin(Position playerPosition) {
        return coins.removeIf(coin -> coin.equals(playerPosition));
    }

    public List<Position> getCoins() {
        return new ArrayList<>(coins);
    }

    public boolean areAllCoinsCollected() {
        return coins.isEmpty();
    }
}