package model.ranking;

public class RankingEntry implements Comparable<RankingEntry> {
    private String playerName;
    private String characterName;
    private int score;
    private int stage;
    private int moveCount;
    private int itemCount;
    private int coinCount;

    public RankingEntry(String playerName, String characterName, int score,
                        int stage, int moveCount, int itemCount, int coinCount) {
        this.playerName = playerName;
        this.characterName = characterName;
        this.score = score;
        this.stage = stage;
        this.moveCount = moveCount;
        this.itemCount = itemCount;
        this.coinCount = coinCount;
    }

    public String getPlayerName() { return playerName; }
    public String getCharacterName() { return characterName; }
    public int getScore() { return score; }
    public int getStage() { return stage; }
    public int getMoveCount() { return moveCount; }
    public int getItemCount() { return itemCount; }
    public int getCoinCount() { return coinCount; }

    @Override
    public int compareTo(RankingEntry other) {
        return Integer.compare(other.score, this.score);
    }
}
