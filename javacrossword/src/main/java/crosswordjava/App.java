package crosswordjava;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Runnable JavaFX application
public class App extends Application {

    private static Stage primaryStage;
    private static GameView gameView;
    private static HelpView helpView;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Create views
        gameView = new GameView();
        helpView = new HelpView();

        // Set initial scene
        Scene scene = new Scene(gameView.getRoot(), 800, 455);
        stage.setTitle("Crossword Puzzle Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void switchToGame() {
        Scene scene = primaryStage.getScene();
        scene.setRoot(gameView.getRoot());
    }

    public static void switchToHelp() {
        Scene scene = primaryStage.getScene();
        scene.setRoot(helpView.getRoot());
    }

    public static void main(String[] args) {
        launch();
    }
}