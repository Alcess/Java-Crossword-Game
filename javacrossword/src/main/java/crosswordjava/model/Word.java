package crosswordjava.model;

/**
 * Represents a word in the crossword puzzle.
 */
public class Word {
    public enum Direction {
        ACROSS, DOWN
    }

    private String word;
    private String clue;
    private int row;
    private int column;
    private Direction direction;
    private int number;

    /**
     * Creates a new word in the crossword puzzle.
     * 
     * @param word      The word text
     * @param clue      The clue for the word
     * @param row       Starting row position
     * @param column    Starting column position
     * @param direction Direction of the word (ACROSS or DOWN)
     * @param number    The number assigned to this word in the puzzle
     */
    public Word(String word, String clue, int row, int column, Direction direction, int number) {
        this.word = word;
        this.clue = clue;
        this.row = row;
        this.column = column;
        this.direction = direction;
        this.number = number;
    }

    /**
     * Gets the word text.
     * 
     * @return The word text
     */
    public String getWord() {
        return word;
    }

    /**
     * Gets the clue for the word.
     * 
     * @return The clue
     */
    public String getClue() {
        return clue;
    }

    /**
     * Gets the starting row position.
     * 
     * @return The row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the starting column position.
     * 
     * @return The column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the direction of the word.
     * 
     * @return The direction (ACROSS or DOWN)
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the number assigned to this word in the puzzle.
     * 
     * @return The word number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the length of the word.
     * 
     * @return The word length
     */
    public int getLength() {
        return word.length();
    }

    /**
     * Checks if the word intersects with another word.
     * 
     * @param other The other word to check
     * @return True if the words intersect, false otherwise
     */
    public boolean intersects(Word other) {
        // Different directions required for intersection
        if (this.direction == other.direction) {
            return false;
        }

        // Check if this word (horizontal) intersects with other word (vertical)
        if (this.direction == Direction.ACROSS && other.direction == Direction.DOWN) {
            return other.row <= this.row && other.row + other.word.length() > this.row &&
                    this.column <= other.column && this.column + this.word.length() > other.column;
        }

        // Check if this word (vertical) intersects with other word (horizontal)
        if (this.direction == Direction.DOWN && other.direction == Direction.ACROSS) {
            return this.row <= other.row && this.row + this.word.length() > other.row &&
                    other.column <= this.column && other.column + other.word.length() > this.column;
        }

        return false;
    }

    /**
     * Gets the character at the intersection point with another word.
     * 
     * @param other The other word
     * @return The character at the intersection, or null if no intersection
     */
    public Character getIntersectionChar(Word other) {
        if (!intersects(other)) {
            return null;
        }

        if (this.direction == Direction.ACROSS) {
            int offset = other.column - this.column;
            return this.word.charAt(offset);
        } else {
            int offset = other.row - this.row;
            return this.word.charAt(offset);
        }
    }
}
