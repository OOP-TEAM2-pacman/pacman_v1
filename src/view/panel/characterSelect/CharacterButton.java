package view.panel.characterSelect;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class CharacterButton extends JButton {
    private final PlayerCharacter character;
    private final int index;
    private static final int SIZE = 80;
    private boolean isSelected;
    private final float[] distances = {0.0f, 0.2f, 1.0f};
    private final Color[] colors = new Color[3];

    public CharacterButton(PlayerCharacter character, int index) {
        this.character = character;
        this.index = index;
        this.isSelected = false;
        setPreferredSize(new Dimension(SIZE, SIZE));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        updateGlowColors(new Color(30, 30, 30, 100));
    }

    private void updateGlowColors(Color baseGlow) {
        colors[0] = baseGlow;
        colors[1] = new Color(baseGlow.getRed(), baseGlow.getGreen(),
                baseGlow.getBlue(), 50);
        colors[2] = new Color(baseGlow.getRed(), baseGlow.getGreen(),
                baseGlow.getBlue(), 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (isSelected) {
            updateGlowColors(Color.decode(character.getColor()));
            int centerX = SIZE / 2;
            int centerY = SIZE / 2;
            float radius = SIZE / 2f;

            Point2D center = new Point2D.Float(centerX, centerY);
            RadialGradientPaint paint = new RadialGradientPaint(
                    center, radius, distances, colors);

            g2.setPaint(paint);
            g2.fillOval(0, 0, SIZE, SIZE);
        }

        if (character.getImage() != null) {
            g2.drawImage(character.getImage(), 10, 10, SIZE-20, SIZE-20, null);
        } else {
            g2.setColor(Color.decode(character.getColor()));
            g2.fillOval(10, 10, SIZE-20, SIZE-20);
        }


        if (isSelected) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(5, 5, SIZE-10, SIZE-10);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics metrics = g2.getFontMetrics();
            String name = character.getName();
            int textWidth = metrics.stringWidth(name);
            int x = (SIZE - textWidth) / 2;

            g2.setColor(Color.BLACK);
            g2.drawString(name, x, SIZE - 5);
        }

        if (getModel().isRollover() && !isSelected) {
            g2.setColor(new Color(255, 255, 255, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(5, 5, SIZE-10, SIZE-10);
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
}
