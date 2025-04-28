package crosswordjava;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import crosswordjava.model.CrosswordGrid;
import crosswordjava.model.GameState;
import crosswordjava.model.Word;

/**
 * Controller for the primary view (main game screen).
 */
public class PrimaryController {
    @FXML
    private GridPane crosswordGridPane;

    @FXML
    private VBox acrossCluesBox;

    @FXML
    private VBox downCluesBox;

    @FXML
    private Button newGameButton;

    @FXML
    private Button checkButton;

    @FXML
    private Button hintButton;

    @FXML
    private Button settingsButton;

    private GameState gameState;
    private TextField[][] gridCells;
    private VBox[][] numberLabels;
    private static final int GRID_SIZE = 10; // Reduced from 15 to 10 for better visibility
    private static final int CELL_SIZE = 45; // Increased from 30 to 45 for larger cells

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        gameState = new GameState(GRID_SIZE);
        gridCells = new TextField[GRID_SIZE][GRID_SIZE];
        numberLabels = new VBox[GRID_SIZE][GRID_SIZE];

        // Initialize the grid UI
        initializeGridUI();

        // Generate a new puzzle
        generateNewPuzzle();

        // Set up button actions
        newGameButton.setOnAction(e -> generateNewPuzzle());
        checkButton.setOnAction(e -> checkPuzzle());
        hintButton.setOnAction(e -> provideHint());
    }

    /**
     * Initializes the grid UI.
     */
    private void initializeGridUI() {
        crosswordGridPane.getChildren().clear();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                TextField cell = createGridCell(row, col);
                gridCells[row][col] = cell;
                crosswordGridPane.add(cell, col, row);
            }
        }
    }

    /**
     * Creates a grid cell.
     * 
     * @param row The row index
     * @param col The column index
     * @return The created TextField
     */
    private TextField createGridCell(int row, int col) {
        TextField cell = new TextField();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setFont(Font.font("System", FontWeight.BOLD, 20)); // Increased from 18 to 22 for better visibility
        cell.setStyle("-fx-alignment: center; -fx-padding: 0;");

        // Limit to one character
        cell.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                cell.setText(newValue.substring(0, 1).toUpperCase());
            } else if (!newValue.isEmpty()) {
                cell.setText(newValue.toUpperCase());
            }

            // Check if the input is correct
            if (!newValue.isEmpty()) {
                char input = newValue.charAt(0);
                if (gameState.checkInput(row, col, input)) {
                    cell.setStyle("-fx-alignment: center; -fx-text-fill: black;");
                    gameState.getGrid().setUserInput(row, col, true);
                } else {
                    cell.setStyle("-fx-alignment: center; -fx-text-fill: red;");
                }

                // Check if the puzzle is complete
                if (gameState.isPuzzleComplete()) {
                    showCompletionMessage();
                }
            }
        });

        return cell;
    }

    /**
     * Generates a new crossword puzzle.
     */
    private void generateNewPuzzle() {
        // Clear the grid
        clearGrid();

        // Generate a new puzzle with backtracking
        boolean success = gameState.generatePuzzleWithBacktracking();
        if (!success) {
            // Fall back to original algorithm if backtracking fails
            success = gameState.generatePuzzle();
            if (!success) {
                showAlert("Error", "Failed to generate a puzzle. Please try again.");
                return;
            }
        }

        // Update the UI
        updateGridUI();
        updateCluesUI();
    }

    /**
     * Clears the grid.
     */
    private void clearGrid() {
        // Clear all elements from the grid pane, including number labels
        crosswordGridPane.getChildren().clear();

        // Reinitialize all grid cells
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                TextField cell = createGridCell(row, col);
                gridCells[row][col] = cell;
                cell.setText("");
                cell.setEditable(false);
                cell.setStyle("-fx-alignment: center; -fx-background-color: black;");

                // Add the new empty cells to the grid
                crosswordGridPane.add(cell, col, row);
            }
        }

        // Clear clue boxes
        acrossCluesBox.getChildren().clear();
        downCluesBox.getChildren().clear();
    }

    /**
     * Updates the grid UI based on the current game state.
     */
    private void updateGridUI() {
        List<Word> words = gameState.getWords();

        // Reset all cells and clear number labels
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                TextField cell = gridCells[row][col];
                cell.setText("");
                cell.setEditable(false);
                cell.setStyle("-fx-alignment: center; -fx-background-color: black;");

                // Reset number labels
                numberLabels[row][col] = null;
            }
        }

        // Mark cells that are part of words
        for (Word word : words) {
            int startRow = word.getRow();
            int startCol = word.getColumn();
            int length = word.getLength();

            // Add word number to the first cell
            if (numberLabels[startRow][startCol] == null) {
                // Create new VBox for this cell
                VBox labelBox = new VBox(2); // 2px spacing between labels
                labelBox.setTranslateX(2);
                labelBox.setTranslateY(2);
                labelBox.setMouseTransparent(true); // Make VBox transparent to mouse events

                Label numberLabel = new Label(String.valueOf(word.getNumber()));
                numberLabel.setFont(Font.font("System", 8));
                numberLabel.setMouseTransparent(true); // Make label transparent to mouse events
                labelBox.getChildren().add(numberLabel);

                numberLabels[startRow][startCol] = labelBox;
                crosswordGridPane.add(labelBox, startCol, startRow);
            } else {
                // Add to existing VBox
                Label numberLabel = new Label(String.valueOf(word.getNumber()));
                numberLabel.setFont(Font.font("System", 8));
                numberLabel.setMouseTransparent(true); // Make label transparent to mouse events
                numberLabels[startRow][startCol].getChildren().add(numberLabel);
            }

            // Mark cells for this word
            for (int i = 0; i < length; i++) {
                int row = (word.getDirection() == Word.Direction.ACROSS) ? startRow : startRow + i;
                int col = (word.getDirection() == Word.Direction.ACROSS) ? startCol + i : startCol;

                if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                    TextField cell = gridCells[row][col];
                    cell.setEditable(true);
                    cell.setStyle("-fx-alignment: center; -fx-background-color: white;");

                    // Ensure TextField has focus capabilities even with overlapping elements
                    cell.setFocusTraversable(true);
                }
            }
        }
    }

    /**
     * Updates the clues UI based on the current game state.
     */
    private void updateCluesUI() {
        acrossCluesBox.getChildren().clear();
        downCluesBox.getChildren().clear();

        // Add header for across clues
        Label acrossHeader = new Label("Across");
        acrossHeader.setFont(Font.font("System", FontWeight.BOLD, 16)); // Reduced from 18 to 16
        acrossCluesBox.getChildren().add(acrossHeader);

        // Add header for down clues
        Label downHeader = new Label("Down");
        downHeader.setFont(Font.font("System", FontWeight.BOLD, 16)); // Reduced from 18 to 16
        downCluesBox.getChildren().add(downHeader);

        // Add across clues
        List<Word> acrossWords = gameState.getWordsInDirection(Word.Direction.ACROSS);
        for (Word word : acrossWords) {
            Label clueLabel = new Label(word.getNumber() + ". " + word.getClue());
            clueLabel.setWrapText(true);
            acrossCluesBox.getChildren().add(clueLabel);
        }

        // Add down clues
        List<Word> downWords = gameState.getWordsInDirection(Word.Direction.DOWN);
        for (Word word : downWords) {
            Label clueLabel = new Label(word.getNumber() + ". " + word.getClue());
            clueLabel.setWrapText(true);
            downCluesBox.getChildren().add(clueLabel);
        }
    }

    /**
     * Checks the puzzle for correctness.
     */
    private void checkPuzzle() {
        CrosswordGrid grid = gameState.getGrid();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                TextField cell = gridCells[row][col];
                if (cell.isEditable()) {
                    if (!cell.getText().isEmpty()) {
                        char input = cell.getText().charAt(0);
                        if (gameState.checkInput(row, col, input)) {
                            // Correct character - green background
                            cell.setStyle("-fx-alignment: center; -fx-background-color: lightgreen;");
                            grid.setUserInput(row, col, true);
                        } else {
                            // Incorrect character - red background
                            cell.setStyle("-fx-alignment: center; -fx-background-color: #ff9999;");
                        }
                    } else {
                        // Empty cell - yellow background
                        cell.setStyle("-fx-alignment: center; -fx-background-color: #ffffaa;");
                    }
                }
            }
        }

        // Check if the puzzle is complete
        if (gameState.isPuzzleComplete()) {
            showCompletionMessage();
        }
    }

    /**
     * Provides a hint by revealing a random cell.
     */
    private void provideHint() {
        List<Word> words = gameState.getWords();
        CrosswordGrid grid = gameState.getGrid();

        // Find a cell that hasn't been filled correctly yet
        for (Word word : words) {
            int startRow = word.getRow();
            int startCol = word.getColumn();
            String wordText = word.getWord();

            for (int i = 0; i < wordText.length(); i++) {
                int row = (word.getDirection() == Word.Direction.ACROSS) ? startRow : startRow + i;
                int col = (word.getDirection() == Word.Direction.ACROSS) ? startCol + i : startCol;

                if (!grid.isUserInput(row, col)) {
                    // Reveal this cell
                    char correctChar = wordText.charAt(i);
                    TextField cell = gridCells[row][col];
                    cell.setText(String.valueOf(correctChar));
                    cell.setStyle("-fx-alignment: center; -fx-text-fill: blue;");
                    grid.setUserInput(row, col, true);

                    // Only provide one hint at a time
                    return;
                }
            }
        }

        // If we get here, all cells are filled correctly
        showAlert("Hint", "All cells are already filled correctly!");
    }

    /**
     * Shows a completion message when the puzzle is complete.
     */
    private void showCompletionMessage() {
        showAlert("Congratulations", "You have completed the crossword puzzle!");
    }

    /**
     * Shows an alert dialog.
     * 
     * @param title   The title of the alert
     * @param message The message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Switches to the secondary view.
     * 
     * @throws IOException If the FXML file cannot be loaded
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
