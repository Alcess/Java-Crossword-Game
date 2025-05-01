package crosswordjava;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Parent;

/**
 * Help view - Programmatic replacement for secondary.fxml and
 * SecondaryController
 */
public class HelpView {
    private BorderPane root;
    private Label helpTextLabel;
    private Button backButton;

    /**
     * Creates the help view with all UI components
     */
    public HelpView() {
        createUI();
        setupHelpText();
    }

    /**
     * Returns the root node for this view
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Creates all UI components programmatically
     */
    private void createUI() {
        // Main layout
        root = new BorderPane();

        // TOP: Header
        VBox topContainer = new VBox(8);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(8, 12.5, 8, 12.5));

        Label titleLabel = new Label("Crossword Puzzle - Help");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        topContainer.getChildren().add(titleLabel);
        root.setTop(topContainer);

        // CENTER: Help text in a scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox contentContainer = new VBox(8);
        contentContainer.setPadding(new Insets(8, 12, 8, 12));

        helpTextLabel = new Label();
        helpTextLabel.setWrapText(true);
        helpTextLabel.setStyle("-fx-font-size: 13px;");

        contentContainer.getChildren().add(helpTextLabel);
        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        // BOTTOM: Back button
        HBox bottomContainer = new HBox(10);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(8, 12.5, 8, 12.5));

        backButton = new Button("Back to Game");
        backButton.setStyle("-fx-font-size: 13px;");
        backButton.setOnAction(e -> App.switchToGame());

        bottomContainer.getChildren().add(backButton);
        root.setBottom(bottomContainer);
    }

    /**
     * Sets up the help text.
     */
    private void setupHelpText() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("Crossword Puzzle Game - Help\n\n");
        helpText.append("How to Play:\n");
        helpText.append("1. Click on a cell in the grid to select it.\n");
        helpText.append("2. Type a letter to fill in the selected cell.\n");
        helpText.append("3. Use the clues provided to solve the puzzle.\n\n");

        helpText.append("Controls:\n");
        helpText.append("- New Game: Generate a new puzzle.\n");
        helpText.append("- Check: Check your answers for correctness.\n");
        helpText.append("- Hint: Reveal a random cell to help you progress.\n\n");

        helpText.append("Tips:\n");
        helpText.append("- Start with shorter words as they are usually easier to solve.\n");
        helpText.append("- Look for intersections between words to help narrow down possibilities.\n");
        helpText.append("- If you're stuck, use the Hint button to get a clue.\n");

        helpTextLabel.setText(helpText.toString());
    }
}
