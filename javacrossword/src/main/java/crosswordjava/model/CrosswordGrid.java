package crosswordjava.model;

/**
 * Represents the crossword puzzle grid.
 */
public class CrosswordGrid {
    private char[][] grid;
    private boolean[][] userInput;
    private int rows;
    private int columns;

    /**
     * Creates a new crossword grid with the specified dimensions.
     * 
     * @param rows    Number of rows in the grid
     * @param columns Number of columns in the grid
     */
    public CrosswordGrid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new char[rows][columns];
        this.userInput = new boolean[rows][columns];

        // Initialize grid with empty cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = ' ';
                userInput[i][j] = false;
            }
        }
    }

    /**
     * Gets the character at the specified position.
     * 
     * @param row Row index
     * @param col Column index
     * @return The character at the specified position
     */
    public char getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return ' ';
    }

    /**
     * Sets the character at the specified position.
     * 
     * @param row Row index
     * @param col Column index
     * @param c   Character to set
     */
    public void setCell(int row, int col, char c) {
        if (isValidPosition(row, col)) {
            grid[row][col] = c;
        }
    }

    /**
     * Checks if the specified position is valid.
     * 
     * @param row Row index
     * @param col Column index
     * @return True if the position is valid, false otherwise
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    /**
     * Marks a cell as filled by the user.
     * 
     * @param row    Row index
     * @param col    Column index
     * @param filled Whether the cell is filled by the user
     */
    public void setUserInput(int row, int col, boolean filled) {
        if (isValidPosition(row, col)) {
            userInput[row][col] = filled;
        }
    }

    /**
     * Checks if a cell is filled by the user.
     * 
     * @param row Row index
     * @param col Column index
     * @return True if the cell is filled by the user, false otherwise
     */
    public boolean isUserInput(int row, int col) {
        if (isValidPosition(row, col)) {
            return userInput[row][col];
        }
        return false;
    }

    /**
     * Gets the number of rows in the grid.
     * 
     * @return Number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns in the grid.
     * 
     * @return Number of columns
     */
    public int getColumns() {
        return columns;
    }
}
