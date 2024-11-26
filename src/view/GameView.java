package view;

import service.GameService;
import view.panel.GamePanel;
import view.panel.RankingPanel;
import view.panel.StartPanel;
import view.panel.characterSelect.CharacterSelectPanel;
import view.panel.characterSelect.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class GameView extends JFrame {
    private final GamePanel gamePanel;
    private final StartPanel startPanel;
    private final CharacterSelectPanel characterSelectPanel;
    private final RankingPanel rankingPanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private GameService model;
    private PlayerCharacter selectedCharacter;

    public GameView() {
        setTitle("PacMan Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gamePanel = new GamePanel();
        startPanel = new StartPanel();
        characterSelectPanel = new CharacterSelectPanel();
        rankingPanel = new RankingPanel();

        mainPanel.add(startPanel, "Start");
        mainPanel.add(characterSelectPanel, "CharacterSelect");
        mainPanel.add(gamePanel, "Game");
        mainPanel.add(rankingPanel, "Ranking");

        add(mainPanel);

        setupNavigationEvents();

        pack();
        setLocationRelativeTo(null);
    }

    private void setupNavigationEvents() {
        startPanel.getStartButton().addActionListener(e -> {
            if (startPanel.getPlayerName().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your name!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            characterSelectPanel.setPlayerName(startPanel.getPlayerName());
            cardLayout.show(mainPanel, "CharacterSelect");
        });

        // 캐릭터 선택 화면에서 게임 화면으로
        characterSelectPanel.setStartButtonListener(e -> {
            if (characterSelectPanel.getPlayerName().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your name!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (model != null) {
                model.setPlayerName(characterSelectPanel.getPlayerName());
            }

            selectedCharacter = characterSelectPanel.getSelectedCharacter();
            gamePanel.setSelectedCharacter(selectedCharacter);
            showGameScreen();
        });

        // 시작화면의 랭킹 버튼
        startPanel.getRankingButton().addActionListener(e -> {
            rankingPanel.updateTable();
            cardLayout.show(mainPanel, "Ranking");
        });

        // 랭킹화면의 뒤로가기 버튼
        rankingPanel.setBackButtonListener(e -> {
            cardLayout.show(mainPanel, "Start");
        });
    }

    public void setModel(GameService model) {
        this.model = model;
        gamePanel.setModel(model);
    }

    public void addKeyListener(KeyListener listener) {
        gamePanel.addKeyListener(listener);
    }

    public String getPlayerName() {
        return startPanel.getPlayerName();
    }

    public void showStartScreen() {
        cardLayout.show(mainPanel, "Start");
    }

    public void showGameScreen() {
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocus();
    }

    public void refresh() {
        gamePanel.repaint();
    }

    public StartPanel getStartPanel() {
        return startPanel;
    }

    public void showGameOverMessage() {
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                    "Game Over!\nFinal Score: %d\nStage: %d\nMoves: %d\nDo you want to try again?",
                    model.calculateFinalScore(),
                    model.getStage(),
                    model.getMoveCount()
            );
            int option = JOptionPane.showConfirmDialog(
                    this, message, "Game Over", JOptionPane.YES_NO_OPTION
            );

            rankingPanel.addRanking(
                    model.getPlayerName(),
                    selectedCharacter.getName(),
                    model.calculateFinalScore(),
                    model.getStage(),
                    model.getMoveCount(),
                    model.getItemCount(),
                    model.getCoinCount()
            );

            if (option == JOptionPane.YES_OPTION) {
                showStartScreen();
            } else {
                System.exit(0);
            }
        });
    }

    public void showGameClearMessage() {
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                    "Congratulations!\nYou've cleared all stages!\n" +
                            "Final Score: %d\nMoves: %d",
                    model.calculateFinalScore(),
                    model.getMoveCount()
            );
            JOptionPane.showMessageDialog(
                    this, message, "Game Clear!", JOptionPane.INFORMATION_MESSAGE
            );

            // 랭킹에 기록 추가
            rankingPanel.addRanking(
                    model.getPlayerName(),
                    selectedCharacter.getName(),
                    model.calculateFinalScore(),
                    model.getStage(),
                    model.getMoveCount(),
                    model.getItemCount(),
                    model.getCoinCount()
            );

            showStartScreen();
        });
    }
}