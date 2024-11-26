package view.panel;

import constant.GameConstants;
import controller.MusicPlayer;
import service.GameService;
import model.Position;
import model.ItemType;
import view.panel.characterSelect.PlayerCharacter;
import view.util.GameColors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GamePanel extends JPanel {
    private GameService model;
    private MusicPlayer musicPlayer;
    private JButton bgmButton, rankingButton, homeButton;
    private BufferedImage wallImage;
    private BufferedImage playerImage;
    private BufferedImage enemyImage;
    private BufferedImage coinImage;
    private BufferedImage shieldItemImage;
    private BufferedImage freezeItemImage;
    private BufferedImage boostItemImage;
    private PlayerCharacter selectedCharacter;
    private BufferedImage backgroundImage;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConstants.WINDOW_SIZE + GameConstants.SIDEBAR_WIDTH, GameConstants.WINDOW_SIZE));
        setBackground(GameColors.BACKGROUND);
        setFocusable(true);

        musicPlayer = new MusicPlayer("resources/Free Music Retro Land.wav");
        musicPlayer.start();

        loadImages();
        initButtons();
    }

    private void loadImages() {
        try {
            wallImage = ImageIO.read(new File("resources/images/wall.png"));
            enemyImage = ImageIO.read(new File("resources/images/enemy.png"));
            coinImage = ImageIO.read(new File("resources/images/coin.png"));
            shieldItemImage = ImageIO.read(new File("resources/images/shield.png"));
            freezeItemImage = ImageIO.read(new File("resources/images/freeze.png"));
            boostItemImage = ImageIO.read(new File("resources/images/boost.png"));
            backgroundImage = ImageIO.read(new File("resources/images/backgroundImages/game_background.png")); // 배경 사진
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("이미지 로드 실패: " + e.getMessage());
        }
    }

    private void initButtons() {
        bgmButton = new JButton("BGM ON/OFF");
        homeButton = new JButton("HOME");

        setupButton(bgmButton);
        setupButton(homeButton);

        bgmButton.setBounds(GameConstants.WINDOW_SIZE + 10,
                GameConstants.WINDOW_SIZE - 100,
                GameConstants.SIDEBAR_WIDTH - 20, 40);
        homeButton.setBounds(GameConstants.WINDOW_SIZE + 10,
                GameConstants.WINDOW_SIZE - 50,
                GameConstants.SIDEBAR_WIDTH - 20, 40);

        bgmButton.addActionListener(e -> {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
                bgmButton.setText("BGM OFF");
            } else {
                musicPlayer.start();
                bgmButton.setText("BGM ON");
            }
            requestFocusInWindow();
        });


        homeButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "정말로 게임을 종료하시겠습니까?",
                    "게임 종료",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                CardLayout layout = (CardLayout) getParent().getLayout();
                layout.show(getParent(), "Start");
            }
            requestFocusInWindow();
        });

        setLayout(null);
        add(bgmButton);
        add(homeButton);
    }

    private void setupButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
    }

    public void setSelectedCharacter(PlayerCharacter character) {
        this.selectedCharacter = character;
        if (character != null) {
            if (character.getImage() != null) {
                playerImage = toBufferedImage(character.getImage()
                        .getScaledInstance(GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE,
                                Image.SCALE_SMOOTH));
            }
        }
        repaint();
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        super.paintComponent(g);

        if (model == null) return;

        drawGrid(g);
        drawCoins(g);
        drawItems(g);
        drawEnemies(g);
        drawPlayer(g);

        if (model.isGameOver()) {
            drawGameOver(g);
        } else if (model.isGameClear()) {
            drawGameClear(g);
        }

        drawSidebar(g);
    }

    private void drawGrid(Graphics g) {
        char[][] map = model.getMapData();
        if (map == null) return;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == '#') {
                    if (wallImage != null) {
                        g.drawImage(wallImage,
                                x * GameConstants.CELL_SIZE,
                                y * GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE,
                                this);
                    } else {
                        g.setColor(GameColors.WALL);
                        g.fillRect(x * GameConstants.CELL_SIZE,
                                y * GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE);
                    }
                }
            }
        }
    }

    private void drawPlayer(Graphics g) {
        Position pos = model.getPlayerPosition();
        int x = pos.getX() * GameConstants.CELL_SIZE;
        int y = pos.getY() * GameConstants.CELL_SIZE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 실드 이펙트 (초록색 원)
        if (model.getShieldCount() > 0) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.setStroke(new BasicStroke(3));
            int padding = 5;
            g2d.drawOval(x - padding,
                    y - padding,
                    GameConstants.CELL_SIZE + padding * 2,
                    GameConstants.CELL_SIZE + padding * 2);
        }

        // 부스트 이펙트 (노란색 빛나는 효과)
        if (model.getBoostTime() > 0) {
            // 외부 원
            Color glowColor = new Color(255, 215, 0, 50);
            g2d.setColor(glowColor);
            int glowSize = 10;
            g2d.fillOval(x - glowSize,
                    y - glowSize,
                    GameConstants.CELL_SIZE + glowSize * 2,
                    GameConstants.CELL_SIZE + glowSize * 2);

            // 내부 원
            glowColor = new Color(255, 215, 0, 100);
            g2d.setColor(glowColor);
            glowSize = 5;
            g2d.fillOval(x - glowSize,
                    y - glowSize,
                    GameConstants.CELL_SIZE + glowSize * 2,
                    GameConstants.CELL_SIZE + glowSize * 2);
        }

        // 캐릭터 그리기
        if (selectedCharacter != null && playerImage != null) {
            g.drawImage(playerImage, x, y,
                    GameConstants.CELL_SIZE,
                    GameConstants.CELL_SIZE, this);
        } else {
            g.setColor(GameColors.PLAYER);
            g.fillOval(x + 5, y + 5,
                    GameConstants.CELL_SIZE - 10,
                    GameConstants.CELL_SIZE - 10);
        }
    }

    private void drawEnemies(Graphics g) {
        for (Position enemy : model.getEnemies()) {
            if (enemyImage != null) {
                g.drawImage(enemyImage,
                        enemy.getX() * GameConstants.CELL_SIZE,
                        enemy.getY() * GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        this);
            } else {
                g.setColor(GameColors.ENEMY);
                g.fillOval(enemy.getX() * GameConstants.CELL_SIZE + 5,
                        enemy.getY() * GameConstants.CELL_SIZE + 5,
                        GameConstants.CELL_SIZE - 10,
                        GameConstants.CELL_SIZE - 10);
            }
        }
    }

    private void drawCoins(Graphics g) {
        for (Position coin : model.getCoins()) {
            if (coinImage != null) {
                g.drawImage(coinImage,
                        coin.getX() * GameConstants.CELL_SIZE,
                        coin.getY() * GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        this);
            } else {
                g.setColor(GameColors.COIN);
                g.fillOval(coin.getX() * GameConstants.CELL_SIZE + 20,
                        coin.getY() * GameConstants.CELL_SIZE + 20,
                        GameConstants.CELL_SIZE - 40,
                        GameConstants.CELL_SIZE - 40);
            }
        }
    }

    private void drawItems(Graphics g) {
        List<Position> items = model.getItems();
        List<ItemType> itemTypes = model.getItemTypes();

        for (int i = 0; i < items.size(); i++) {
            Position item = items.get(i);
            ItemType type = itemTypes.get(i);
            BufferedImage itemImage = null;
            Color itemColor = null;

            switch (type) {
                case SHIELD:
                    itemImage = shieldItemImage;
                    itemColor = GameColors.SHIELD_ITEM;
                    break;
                case FREEZE:
                    itemImage = freezeItemImage;
                    itemColor = GameColors.FREEZE_ITEM;
                    break;
                case BOOST:
                    itemImage = boostItemImage;
                    itemColor = GameColors.BOOST_ITEM;
                    break;
            }

            if (itemImage != null) {
                g.drawImage(itemImage,
                        item.getX() * GameConstants.CELL_SIZE,
                        item.getY() * GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        GameConstants.CELL_SIZE,
                        this);
            } else {
                drawDefaultItem(g, item, type, itemColor);
            }
        }
    }

    private void drawDefaultItem(Graphics g, Position item, ItemType type, Color color) {
        g.setColor(color);
        int x = item.getX() * GameConstants.CELL_SIZE + 15;
        int y = item.getY() * GameConstants.CELL_SIZE + 15;
        int size = GameConstants.CELL_SIZE - 30;

        switch (type) {
            case SHIELD -> g.fillRect(x, y, size, size);
            case FREEZE -> g.fillOval(x, y, size, size);
            case BOOST -> {
                int[] xPoints = {x + size / 2, x, x + size / 2, x + size};
                int[] yPoints = {y, y + size / 2, y + size, y + size / 2};
                g.fillPolygon(xPoints, yPoints, 4);
            }
        }
    }

    private void drawSidebar(Graphics g) {
        g.setColor(GameColors.SIDEBAR_BACKGROUND);
        g.fillRect(GameConstants.WINDOW_SIZE, 0,
                GameConstants.SIDEBAR_WIDTH, GameConstants.WINDOW_SIZE);

        // 선택한 캐릭터 표시 영역
        int characterBoxSize = 80;
        int characterBoxX = GameConstants.WINDOW_SIZE + (GameConstants.SIDEBAR_WIDTH - characterBoxSize) / 2;
        int characterBoxY = 20;

        // 캐릭터 배경 (약간 어두운 배경)
        g.setColor(new Color(50, 50, 50));
        g.fillRect(characterBoxX, characterBoxY, characterBoxSize, characterBoxSize);

        // 캐릭터 이미지 또는 기본 모양 그리기
        if (selectedCharacter != null) {
            if (playerImage != null) {
                g.drawImage(playerImage,
                        characterBoxX + 5,
                        characterBoxY + 5,
                        characterBoxSize - 10,
                        characterBoxSize - 10,
                        null);
            } else {
                g.setColor(Color.decode(selectedCharacter.getColor()));
                g.fillOval(characterBoxX + 5,
                        characterBoxY + 5,
                        characterBoxSize - 10,
                        characterBoxSize - 10);
            }

            // 캐릭터 이름
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String characterName = selectedCharacter.getName();
            FontMetrics metrics = g.getFontMetrics();
            int nameX = GameConstants.WINDOW_SIZE + (GameConstants.SIDEBAR_WIDTH - metrics.stringWidth(characterName)) / 2;
            g.drawString(characterName, nameX, characterBoxY + characterBoxSize + 20);
        }

        // 게임 정보 표시
        g.setColor(GameColors.TEXT);
        g.setFont(new Font("Arial", Font.PLAIN, 14));

        // 시작 y 위치를 캐릭터 표시 영역 아래로 조정
        int y = characterBoxY + characterBoxSize + 50;
        int lineHeight = 25;

        // 정보 표시
        drawSidebarText(g, "Player: " + model.getPlayerName(), y);
        y += lineHeight;

        drawSidebarText(g, "Score: " + model.getScore(), y);
        y += lineHeight;

        drawSidebarText(g, "Stage: " + model.getStage(), y);
        y += lineHeight;

        drawSidebarText(g, "Moves: " + model.getMoveCount(), y);
        y += lineHeight;

        drawSidebarText(g, "Shield: " + model.getShieldCount(), y);
        y += lineHeight;

        if (model.getTimeCount() > 0) {
            g.setColor(GameColors.FREEZE_ITEM);
            drawSidebarText(g, "Freeze Time: " + model.getTimeCount(), y);
            y += lineHeight;
        }

        if (model.getBoostTime() > 0) {
            g.setColor(GameColors.BOOST_ITEM);
            drawSidebarText(g, "Boost Time: " + model.getBoostTime(), y);
        }
    }

    private void drawSidebarText(Graphics g, String text, int y) {
        int x = GameConstants.WINDOW_SIZE + 10;
        g.drawString(text, x, y);
    }

    private void drawGameOver(Graphics g) {
        drawCenteredMessage(g, "Game Over!");
        drawScore(g);
    }

    private void drawGameClear(Graphics g) {
        drawCenteredMessage(g, "You Win!");
        drawScore(g);
    }

    private void drawCenteredMessage(Graphics g, String message) {
        g.setColor(GameColors.TEXT);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = g.getFontMetrics();
        int x = (GameConstants.WINDOW_SIZE - metrics.stringWidth(message)) / 2;
        int y = GameConstants.WINDOW_SIZE / 2;
        g.drawString(message, x, y);
    }

    private void drawScore(Graphics g) {
        String scoreMessage = "Final Score: " + model.getScore();
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        int x = (GameConstants.WINDOW_SIZE - metrics.stringWidth(scoreMessage)) / 2;
        int y = GameConstants.WINDOW_SIZE / 2 + 40;
        g.drawString(scoreMessage, x, y);
    }

    public void setModel(GameService model) {
        this.model = model;
        repaint();
    }
}