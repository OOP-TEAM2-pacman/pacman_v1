// ItemService.java
package service;

import model.ItemType;
import model.Map;
import model.Position;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ItemService {
    private final Random random;
    private List<Position> items;
    private List<ItemType> itemTypes;

    public ItemService() {
        this.random = new Random();
        this.items = new ArrayList<>();
        this.itemTypes = new ArrayList<>();
    }

    public void initializeItems(Map gameMap, Position playerPosition,
                                List<Position> enemies, List<Position> coins, int stage) {
        items.clear();
        itemTypes.clear();
        int numItems = stage + 2;

        for (int i = 0; i < numItems; i++) {
            Position item;
            do {
                item = new Position(
                        random.nextInt(gameMap.getWidth()),
                        random.nextInt(gameMap.getHeight())
                );
            } while (!isValidItemPosition(item, gameMap, playerPosition, enemies, coins));

            items.add(item);
            itemTypes.add(generateRandomItemType());
        }
    }

    private boolean isValidItemPosition(Position pos, Map gameMap,
                                        Position playerPosition,
                                        List<Position> enemies,
                                        List<Position> coins) {
        return gameMap.isEmpty(pos) &&
                !coins.contains(pos) &&
                !enemies.contains(pos) &&
                !pos.equals(playerPosition) &&
                !items.contains(pos);
    }

    private ItemType generateRandomItemType() {
        int randomValue = random.nextInt(10);
        if (randomValue < 4) return ItemType.SHIELD;
        if (randomValue < 8) return ItemType.FREEZE;
        return ItemType.BOOST;
    }

    public List<Position> getItems() {
        return new ArrayList<>(items);
    }

    public List<ItemType> getItemTypes() {
        return new ArrayList<>(itemTypes);
    }

    public ItemType collectItem(Position playerPosition) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).equals(playerPosition)) {
                ItemType type = itemTypes.get(i);
                items.remove(i);
                itemTypes.remove(i);
                return type;
            }
        }
        return null;
    }
}