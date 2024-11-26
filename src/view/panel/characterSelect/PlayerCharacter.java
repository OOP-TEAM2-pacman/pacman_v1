package view.panel.characterSelect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlayerCharacter {
    private final String name;
    private final String color;
    private Image image;

    public PlayerCharacter(String name, String color) {
        this.name = name;
        this.color = color;
        loadImage();
    }

    private void loadImage() {
        try {
            String imagePath = "resources/playerImages/" + name.toLowerCase() + ".png";
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Failed to load image for " + name + ": " + e.getMessage());
            image = null;
        }
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public Image getImage() { return image; }
}