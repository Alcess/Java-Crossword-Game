package crosswordjava;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the secondary view (settings/help screen).
 */
public class SecondaryController {
    @FXML
    private Button backButton;

    @FXML
    private Label helpTextLabel;

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        // Set up the help text
        setupHelpText();
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
        helpTextLabel.setWrapText(true);
    }

    /**
     * Switches to the primary view.
     * 
     * @throws IOException If the FXML file cannot be loaded
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
