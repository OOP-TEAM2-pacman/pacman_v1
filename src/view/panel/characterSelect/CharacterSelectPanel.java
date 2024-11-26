package view.panel.characterSelect;

import constant.GameConstants;
import view.util.GameColors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectPanel extends JPanel {
    private final List<PlayerCharacter> characters;
    private final List<CharacterButton> characterButtons;
    private int selectedCharacterIndex = 0;
    private final JButton startButton;
    private final JTextField nameField;
    private final JLabel descriptionLabel; // 추가
    private static BufferedImage backgroundImage;

    public CharacterSelectPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(GameColors.BACKGROUND);
        setPreferredSize(new Dimension(GameConstants.WINDOW_SIZE + GameConstants.SIDEBAR_WIDTH, GameConstants.WINDOW_SIZE));

        characters = initializeCharacters();
        characterButtons = new ArrayList<>();

        // 제목
        JLabel titleLabel = createStyledLabel("Choose Your Character", 30);

        // 이름 입력 패널
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setPreferredSize(new Dimension(300, 50));
        namePanel.setMaximumSize(new Dimension(300, 50));

        JLabel nameLabel = new JLabel("Enter Name: ");
        nameLabel.setForeground(Color.BLACK); // 이름도 검정색으로 설정
        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // 캐릭터 선택 패널
        JPanel characterPanel = new JPanel();
        characterPanel.setOpaque(false);
        characterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        characterPanel.setPreferredSize(new Dimension(600, 200));
        characterPanel.setMaximumSize(new Dimension(600, 200));

        // 캐릭터 버튼 추가
        for (int i = 0; i < characters.size(); i++) {
            CharacterButton charButton = new CharacterButton(characters.get(i), i);
            characterButtons.add(charButton);
            final int index = i;

            charButton.addActionListener(e -> {
                updateSelectedCharacter(index);
            });

            // 첫 번째 캐릭터 기본 선택
            if (i == 0) {
                charButton.setSelected(true);
            }

            characterPanel.add(charButton);
        }

        // 시작 버튼
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(200, 50));
        styleButton(startButton);

        // 캐릭터 설명 패널
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setOpaque(false);
        descriptionPanel.setPreferredSize(new Dimension(600, 50));
        descriptionPanel.setMaximumSize(new Dimension(600, 50));
        descriptionLabel = createStyledLabel(getCharacterDescription(0), 14);
        descriptionLabel.setForeground(Color.BLACK);  // 설명도 검정색으로 설정
        descriptionPanel.add(descriptionLabel);

        // 컴포넌트 추가
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(30));
        add(namePanel);
        add(Box.createVerticalStrut(30));
        add(characterPanel);
        add(Box.createVerticalStrut(10));
        add(descriptionPanel);
        add(Box.createVerticalStrut(20));
        add(startButton);
        add(Box.createVerticalGlue());

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

    private void updateSelectedCharacter(int index) {
        // 이전 선택 해제
        characterButtons.get(selectedCharacterIndex).setSelected(false);
        // 새로운 선택 설정
        selectedCharacterIndex = index;
        characterButtons.get(selectedCharacterIndex).setSelected(true);

        // 설명 업데이트
        descriptionLabel.setText(getCharacterDescription(index));
        descriptionLabel.revalidate();
        descriptionLabel.repaint();
    }

    private String getCharacterDescription(int index) {
        return switch (index) {
            case 0 -> "Classic: The Pac-Man that we know~";
            case 1 -> "Ninja: It's a character that gives you a very fast feeling!";
            case 2 -> "Robot: It looks very durable!";
            case 3 -> "Cat: Cute!";
            case 4 -> "Astronaut: It's not an avocado!";
            case 5 -> "Wizard: I'm a wizard, but I don't know how to use magic!";
            default -> "";
        };
    }

    private List<PlayerCharacter> initializeCharacters() {
        List<PlayerCharacter> chars = new ArrayList<>();
        chars.add(new PlayerCharacter("Classic", "#FFD700"));
        chars.add(new PlayerCharacter("Ninja", "#4A4A4A"));
        chars.add(new PlayerCharacter("Robot", "#3F51B5"));
        chars.add(new PlayerCharacter("Cat", "#FFA726"));
        chars.add(new PlayerCharacter("Astronaut", "#81C784"));
        chars.add(new PlayerCharacter("Wizard", "#9C27B0"));
        return chars;
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });
    }

    public void setStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }

    public String getPlayerName() {
        return nameField.getText().trim();
    }

    public void setPlayerName(String name) {
        nameField.setText(name);
    }

    public PlayerCharacter getSelectedCharacter() {
        return characters.get(selectedCharacterIndex);
    }
}
