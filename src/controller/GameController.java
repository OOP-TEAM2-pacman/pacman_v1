package controller;

import service.GameService;
import model.Direction;
import view.GameView;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class GameController implements KeyListener {
    private final GameService model;
    private final GameView view;

    public GameController(GameService model, GameView view) {
        this.model = model;
        this.view = view;

        view.setModel(model);
        view.addKeyListener(this);

        view.getStartPanel().getStartButton().addActionListener(e -> {
            try {
                startGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void startGame() throws IOException {
        String playerName = view.getPlayerName();
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a nickname!");
            return;
        }

        model.setPlayerName(playerName);
        model.initializeGame();
        view.showGameScreen();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction direction = switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> Direction.UP;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> Direction.DOWN;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> Direction.LEFT;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> Direction.RIGHT;
            default -> null;
        };

        if (direction != null) {
            model.movePlayer(direction);

            if (model.isGameOver()) {
                view.showGameOverMessage();
            } else if (model.isGameClear()) {
                view.showGameClearMessage();
            }

            view.refresh();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}