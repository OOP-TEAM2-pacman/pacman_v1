package view.panel;

import constant.GameConstants;
import view.util.GameColors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StartPanel extends JPanel {
    private final JTextField nameField;
    private final JButton startButton;
    private final JButton rankingButton;
    private static BufferedImage backgroundImage;

    public StartPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(GameColors.BACKGROUND);
        setPreferredSize(new Dimension(GameConstants.WINDOW_SIZE, GameConstants.WINDOW_SIZE));

        // Title
        JLabel titleLabel = new JLabel("PacMan Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(GameColors.TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name input
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Enter Nickname: ");
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setBackground(Color.WHITE);
        nameField.setForeground(Color.BLACK);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Input panel 크기 제한
        inputPanel.setMaximumSize(new Dimension(250, 100));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(nameLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(startButton);

        rankingButton = new JButton("Rankings");
        rankingButton.setFont(new Font("Arial", Font.BOLD, 20));
        rankingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(rankingButton);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(50));
        add(inputPanel);
        add(Box.createVerticalStrut(30));
        add(startButton);
        add(Box.createVerticalStrut(15));
        add(rankingButton);
        add(Box.createVerticalGlue());

        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("resources/images/backgroundImages/game_background.png"));
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
        button.setMaximumSize(new Dimension(200, 50));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);

        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70));
            }
        });
    }

    public String getPlayerName() {
        return nameField.getText().trim();
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getRankingButton() {
        return rankingButton;
    }

}