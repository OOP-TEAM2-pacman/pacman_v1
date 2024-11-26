package view.panel;

import model.ranking.RankingEntry;
import view.util.GameColors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class RankingPanel extends JPanel {
    private final JTable rankingTable;
    private final DefaultTableModel tableModel;
    private final JButton backButton;
    private static final String RANKING_FILE = "rankings.txt";
    private static BufferedImage backgroundImage;

    public RankingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GameColors.BACKGROUND);
        setPreferredSize(new Dimension(800, 600));

        // 제목
        JLabel titleLabel = new JLabel("Player Rankings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 테이블 열 설정
        String[] columns = {
                "Rank", "Player", "Character", "Score", "Stage",
                "Moves", "Items", "Coins"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(tableModel);
        setupTable();

        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBackground(GameColors.BACKGROUND);
        scrollPane.getViewport().setBackground(GameColors.BACKGROUND);

        // 뒤로가기 버튼
        backButton = new JButton("Back to Menu");
        styleButton(backButton);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // 버튼 여백 제거
        buttonPanel.setOpaque(false); // 버튼 패널 배경 투명
        buttonPanel.add(backButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("resources/images/backgroundImages/game_background.png")); // 배경 이미지 추가
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("배경 이미지 로드 실패: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void styleButton(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
    }

    private void setupTable() {
        rankingTable.setBackground(new Color(40, 40, 40));
        rankingTable.setForeground(Color.WHITE);
        rankingTable.setGridColor(new Color(70, 70, 70));
        rankingTable.setSelectionBackground(new Color(60, 60, 60));
        rankingTable.setSelectionForeground(Color.WHITE);
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 14));
        rankingTable.setRowHeight(30);

        JTableHeader header = rankingTable.getTableHeader();
        header.setBackground(new Color(50, 50, 50));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Player
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Character
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Score
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Stage
        rankingTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Moves
        rankingTable.getColumnModel().getColumn(6).setPreferredWidth(60);  // Items
        rankingTable.getColumnModel().getColumn(7).setPreferredWidth(60);  // Coins
    }

    public void updateRankings(List<RankingEntry> rankings) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (RankingEntry entry : rankings) {
            Object[] rowData = {
                    rank++,
                    entry.getPlayerName(),
                    entry.getCharacterName(),
                    entry.getScore(),
                    entry.getStage(),
                    entry.getMoveCount(),
                    entry.getItemCount(),
                    entry.getCoinCount()
            };
            tableModel.addRow(rowData);
        }
    }

    public void addRanking(String playerName, String characterName,
                           int score, int stage, int moveCount,
                           int itemCount, int coinCount) {
        List<RankingEntry> rankings = loadRankings();
        rankings.add(new RankingEntry(playerName, characterName, score,
                stage, moveCount, itemCount, coinCount));
        Collections.sort(rankings);

        if (rankings.size() > 10) {
            rankings = rankings.subList(0, 10);
        }

        saveRankings(rankings);
        updateTable();
    }

    public void updateTable() {
        List<RankingEntry> rankings = loadRankings();
        updateRankings(rankings);
    }

    private List<RankingEntry> loadRankings() {
        List<RankingEntry> rankings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RANKING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
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
            System.out.println("Failed to load rankings: " + e.getMessage());
        }
        return rankings;
    }

    private void saveRankings(List<RankingEntry> rankings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RANKING_FILE))) {
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
            System.out.println("Failed to save rankings: " + e.getMessage());
        }
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
