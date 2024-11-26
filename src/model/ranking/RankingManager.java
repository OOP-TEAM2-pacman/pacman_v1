package model.ranking;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingManager {
    private static final List<RankingEntry> rankings = new ArrayList<>();
    private static final String SAVE_FILE = "resources/rankings.txt";
    private static final int MAX_RANKINGS = 10;

    public RankingManager() {
        loadRankings();
    }

    public void addRanking(RankingEntry entry) {
        rankings.add(entry);
        Collections.sort(rankings);

        if (rankings.size() > MAX_RANKINGS) {
            rankings.remove(rankings.size() - 1);
        }

        saveRankings();
    }

    public List<RankingEntry> getRankings() {
        return new ArrayList<>(rankings);
    }

    private void saveRankings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (RankingEntry entry : rankings) {
                writer.println(String.format("%s,%s,%d,%d,%d,%d,%d",
                        entry.getPlayerName(),
                        entry.getCharacterName(),
                        entry.getScore(),
                        entry.getStage(),
                        entry.getMoveCount(),
                        entry.getItemCount(),
                        entry.getCoinCount()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRankings() {
        rankings.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    rankings.add(new RankingEntry(
                            parts[0],
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6])
                    ));
                }
            }
        } catch (IOException e) {
            // 파일이 없는 경우는 무시
        }
    }
}