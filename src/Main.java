import service.GameService;
import view.GameView;
import controller.GameController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameService model = new GameService();
            GameView view = new GameView();
            new GameController(model, view);
            view.setVisible(true);
        });
    }
}