package crosswordjava.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Collections;

/**
 * Manages the state of the crossword puzzle game.
 */
public class GameState {
    private CrosswordGrid grid;
    private List<Word> words;
    private Dictionary dictionary;
    private int gridSize;
    private Random random;
    private Set<String> usedWords; // Track used words to prevent duplicates
    private int wordTarget = 20; // Default medium difficulty
    private int backtrackingDepthLimit = 300; // Default backtracking depth for medium difficulty
    private int minRequiredWords = 4; // Minimum words to generate a valid puzzle

    /**
     * Creates a new game state with the specified grid size.
     * 
     * @param gridSize The size of the grid (width and height)
     */
    public GameState(int gridSize) {
        this.gridSize = gridSize;
        this.grid = new CrosswordGrid(gridSize, gridSize);
        this.words = new ArrayList<>();
        this.dictionary = new Dictionary();
        this.random = new Random();
        this.usedWords = new HashSet<>();
    }

    // Set difficulty parameters for the game
    public void setDifficultyParameters(int wordTarget, int depthLimit, int minWords) {
        this.wordTarget = wordTarget;
        this.backtrackingDepthLimit = depthLimit;
        this.minRequiredWords = minWords;
    }

    /**
     * Generates a new crossword puzzle.
     * 
     * @return True if the puzzle was generated successfully, false otherwise
     */
    public boolean generatePuzzle() {
        // Clear existing state
        grid = new CrosswordGrid(gridSize, gridSize);
        words.clear();
        usedWords.clear(); // Clear the set of used words

        // Start with a random word in the middle
        Dictionary.WordEntry firstWordEntry = dictionary.getRandomWord(3, 6);
        if (firstWordEntry == null) {
            return false;
        }

        String firstWord = firstWordEntry.getWord();
        usedWords.add(firstWord); // Add first word to used words set
        int startRow = gridSize / 2;
        int startCol = (gridSize - firstWord.length()) / 2;

        Word word = new Word(firstWord, firstWordEntry.getClue(), startRow, startCol, Word.Direction.ACROSS, 1);
        words.add(word);
        placeWordOnGrid(word);

        // Try to add more words
        int maxAttempts = 100;
        int wordNumber = 2;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Try to find a word that intersects with an existing word
            Word intersectingWord = findIntersectingWord(wordNumber);

            if (intersectingWord != null) {
                words.add(intersectingWord);
                placeWordOnGrid(intersectingWord);
                usedWords.add(intersectingWord.getWord()); // Add to used words set
                wordNumber++;

                // Stop if we have enough words
                if (words.size() >= minRequiredWords) {
                    break;
                }
            }
        }

        return words.size() >= 4;
    }

    /**
     * Finds a word that intersects with an existing word.
     * Optimized to maximize intersections and minimize empty cells.
     */
    private Word findIntersectingWord(int wordNumber) {
        // Pick a random existing word to intersect with
        if (words.isEmpty()) {
            return null;
        }

        // Select a word with more intersection potential
        Word existingWord = selectWordWithMoreIntersections();
        String wordText = existingWord.getWord();

        // Try every position in the existing word for maximum coverage
        List<Integer> positionsToTry = new ArrayList<>();
        for (int i = 0; i < wordText.length(); i++) {
            positionsToTry.add(i);
        }

        // Shuffle the positions for some randomness
        Collections.shuffle(positionsToTry, random);

        // Try each position in the existing word as an intersection point
        for (int i : positionsToTry) {
            char c = wordText.charAt(i);

            // Try different positions for the new word's intersection
            // Try more positions to increase chance of successful placement
            for (int intersectionPos = 0; intersectionPos < Math.min(5, wordText.length()); intersectionPos++) {
                // Calculate position for the new word
                int row, col;
                Word.Direction direction;

                if (existingWord.getDirection() == Word.Direction.ACROSS) {
                    // Existing word is across, new word will be down
                    row = existingWord.getRow();
                    col = existingWord.getColumn() + i;
                    direction = Word.Direction.DOWN;
                } else {
                    // Existing word is down, new word will be across
                    row = existingWord.getRow() + i;
                    col = existingWord.getColumn();
                    direction = Word.Direction.ACROSS;
                }

                // Try to find a word with this character at the intersection
                // Try more words to increase chances of finding a good match
                List<Dictionary.WordEntry> entries = dictionary.getAllWordsWithCharAt(c, intersectionPos);
                if (entries.isEmpty())
                    continue;

                Collections.shuffle(entries, random);

                for (Dictionary.WordEntry entry : entries) {
                    // Skip if already used
                    if (usedWords.contains(entry.getWord())) {
                        continue;
                    }

                    String newWordText = entry.getWord();

                    // Adjust position so the intersection character aligns
                    if (direction == Word.Direction.ACROSS) {
                        col -= intersectionPos;
                    } else {
                        row -= intersectionPos;
                    }

                    // Check if the word fits on the grid
                    if (!wordFitsOnGrid(newWordText, row, col, direction)) {
                        continue;
                    }

                    // Check if the word overlaps with existing words (except at the intersection)
                    if (wordOverlapsExisting(newWordText, row, col, direction, existingWord, i)) {
                        continue;
                    }

                    // Create and return the new word
                    return new Word(newWordText, entry.getClue(), row, col, direction, wordNumber);
                }
            }
        }

        return null;
    }

    /**
     * Selects a word that has more intersections or intersection potential with
     * other words.
     * This helps create a denser grid with fewer empty cells.
     */
    private Word selectWordWithMoreIntersections() {
        if (words.size() <= 1) {
            return words.get(0);
        }

        // Select 3 random words and pick the one with most intersections or longest
        // word
        List<Word> candidates = new ArrayList<>();
        for (int i = 0; i < Math.min(3, words.size()); i++) {
            Word candidate = words.get(random.nextInt(words.size()));
            if (!candidates.contains(candidate)) {
                candidates.add(candidate);
            }
        }

        // If we couldn't get unique words, just return a random one
        if (candidates.isEmpty()) {
            return words.get(random.nextInt(words.size()));
        }

        // Count intersections for each candidate and pick the one with most
        Word bestCandidate = candidates.get(0);
        int mostIntersections = countIntersections(bestCandidate);
        int longestLength = bestCandidate.getWord().length();

        for (int i = 1; i < candidates.size(); i++) {
            Word candidate = candidates.get(i);
            int intersections = countIntersections(candidate);
            int length = candidate.getWord().length();

            // Prefer words with more intersections, or longer words if tied
            if (intersections > mostIntersections ||
                    (intersections == mostIntersections && length > longestLength)) {
                mostIntersections = intersections;
                longestLength = length;
                bestCandidate = candidate;
            }
        }

        return bestCandidate;
    }

    /**
     * Counts how many intersections a word has with other words.
     *
     * @param word The word to check
     * @return Number of intersections
     */
    private int countIntersections(Word word) {
        int count = 0;
        for (Word other : words) {
            if (word != other && word.intersects(other)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if a word fits on the grid.
     * 
     * @param word      The word text
     * @param row       Starting row
     * @param col       Starting column
     * @param direction The direction of the word
     * @return True if the word fits, false otherwise
     */
    private boolean wordFitsOnGrid(String word, int row, int col, Word.Direction direction) {
        if (row < 0 || col < 0) {
            return false;
        }

        if (direction == Word.Direction.ACROSS) {
            return col + word.length() <= gridSize;
        } else {
            return row + word.length() <= gridSize;
        }
    }

    /**
     * Checks if a word overlaps with existing words (except at the intended
     * intersection).
     * 
     * @param word           The word text
     * @param row            Starting row
     * @param col            Starting column
     * @param direction      The direction of the word
     * @param intersectWord  The word to intersect with
     * @param intersectIndex The index in the intersect word where the intersection
     *                       occurs
     * @return True if there's an overlap, false otherwise
     */
    private boolean wordOverlapsExisting(String word, int row, int col, Word.Direction direction,
            Word intersectWord, int intersectIndex) {
        int intersectRow, intersectCol;

        if (intersectWord.getDirection() == Word.Direction.ACROSS) {
            intersectRow = intersectWord.getRow();
            intersectCol = intersectWord.getColumn() + intersectIndex;
        } else {
            intersectRow = intersectWord.getRow() + intersectIndex;
            intersectCol = intersectWord.getColumn();
        }

        for (int i = 0; i < word.length(); i++) {
            int currentRow = (direction == Word.Direction.ACROSS) ? row : row + i;
            int currentCol = (direction == Word.Direction.ACROSS) ? col + i : col;

            // Skip the intersection point
            if (currentRow == intersectRow && currentCol == intersectCol) {
                continue;
            }

            // Check if this cell is already occupied
            for (Word w : words) {
                if (w == intersectWord) {
                    continue; // Skip the intersect word
                }

                if (isPositionInWord(currentRow, currentCol, w)) {
                    return true; // Overlap found
                }
            }
        }

        return false;
    }

    /**
     * Checks if a position is part of a word.
     * 
     * @param row  The row to check
     * @param col  The column to check
     * @param word The word to check against
     * @return True if the position is part of the word, false otherwise
     */
    private boolean isPositionInWord(int row, int col, Word word) {
        if (word.getDirection() == Word.Direction.ACROSS) {
            if (row != word.getRow()) {
                return false;
            }
            int startCol = word.getColumn();
            return col >= startCol && col < startCol + word.getLength();
        } else {
            if (col != word.getColumn()) {
                return false;
            }
            int startRow = word.getRow();
            return row >= startRow && row < startRow + word.getLength();
        }
    }

    /**
     * Places a word on the grid.
     * 
     * @param word The word to place
     */
    private void placeWordOnGrid(Word word) {
        String text = word.getWord();
        int row = word.getRow();
        int col = word.getColumn();

        for (int i = 0; i < text.length(); i++) {
            if (word.getDirection() == Word.Direction.ACROSS) {
                grid.setCell(row, col + i, text.charAt(i));
            } else {
                grid.setCell(row + i, col, text.charAt(i));
            }
        }
    }

    /**
     * Gets the crossword grid.
     * 
     * @return The grid
     */
    public CrosswordGrid getGrid() {
        return grid;
    }

    /**
     * Gets the list of words in the puzzle.
     * 
     * @return The list of words
     */
    public List<Word> getWords() {
        return words;
    }

    /**
     * Gets the words in the specified direction.
     * 
     * @param direction The direction (ACROSS or DOWN)
     * @return A list of words in the specified direction
     */
    public List<Word> getWordsInDirection(Word.Direction direction) {
        List<Word> result = new ArrayList<>();
        for (Word word : words) {
            if (word.getDirection() == direction) {
                result.add(word);
            }
        }
        return result;
    }

    /**
     * Checks if the user's input is correct for a specific position.
     * 
     * @param row Row index
     * @param col Column index
     * @param c   The character input by the user
     * @return True if the input is correct, false otherwise
     */
    public boolean checkInput(int row, int col, char c) {
        return grid.getCell(row, col) == Character.toUpperCase(c);
    }

    /**
     * Checks if the puzzle is complete.
     * 
     * @return True if all cells are correctly filled, false otherwise
     */
    public boolean isPuzzleComplete() {
        for (Word word : words) {
            String text = word.getWord();
            int row = word.getRow();
            int col = word.getColumn();

            for (int i = 0; i < text.length(); i++) {
                int currentRow = (word.getDirection() == Word.Direction.ACROSS) ? row : row + i;
                int currentCol = (word.getDirection() == Word.Direction.ACROSS) ? col + i : col;

                if (!grid.isUserInput(currentRow, currentCol)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean generatePuzzleWithBacktracking() {
        // Clear existing state
        grid = new CrosswordGrid(gridSize, gridSize);
        words.clear();
        usedWords.clear();

        // Place first word in the middle (same as before)
        Dictionary.WordEntry firstWordEntry = dictionary.getRandomWord(3, 6);
        if (firstWordEntry == null) {
            return false;
        }

        String firstWord = firstWordEntry.getWord();
        usedWords.add(firstWord);
        int startRow = gridSize / 2;
        int startCol = (gridSize - firstWord.length()) / 2;

        Word word = new Word(firstWord, firstWordEntry.getClue(), startRow, startCol, Word.Direction.ACROSS, 1);
        words.add(word);
        placeWordOnGrid(word);

        // Start backtracking from word #2
        return backtrackPlaceWords(2, 0);
    }

    private boolean backtrackPlaceWords(int wordNumber, int depth) {
        // Termination conditions
        if (words.size() >= wordTarget) { // Increased from 12 to 20 for more words
            return true; // Success - we've placed enough words
        }

        if (depth > backtrackingDepthLimit) { // Increased from 150 to 300 for more attempts
            return false; // Prevent excessive recursion
        }

        // Try placing a word with each existing word
        for (Word existingWord : new ArrayList<>(words)) {
            String wordText = existingWord.getWord();

            // Try all positions for maximum density
            List<Integer> positionsToTry = new ArrayList<>();
            for (int i = 0; i < wordText.length(); i++) {
                positionsToTry.add(i);
            }

            // Shuffle the positions for randomness
            Collections.shuffle(positionsToTry, random);

            // Try each position in the existing word
            for (int i : positionsToTry) {
                char c = wordText.charAt(i);

                // Try more positions for the intersecting character
                for (int newWordCharPos = 0; newWordCharPos < Math.min(5, wordText.length()); newWordCharPos++) {
                    // Get all words with this character at this position
                    List<Dictionary.WordEntry> entries = dictionary.getAllWordsWithCharAt(c, newWordCharPos);
                    if (entries.isEmpty())
                        continue;

                    // Shuffle to try different words
                    Collections.shuffle(entries, random);

                    for (Dictionary.WordEntry entry : entries) {
                        if (usedWords.contains(entry.getWord())) {
                            continue;
                        }

                        // Calculate placement position
                        Word.Direction direction = (existingWord.getDirection() == Word.Direction.ACROSS)
                                ? Word.Direction.DOWN
                                : Word.Direction.ACROSS;

                        int row = existingWord.getRow();
                        int col = existingWord.getColumn();

                        // Adjust for intersection point
                        if (existingWord.getDirection() == Word.Direction.ACROSS) {
                            col += i;
                            row -= newWordCharPos;
                        } else {
                            row += i;
                            col -= newWordCharPos;
                        }

                        // Check if placement is valid
                        if (!wordFitsOnGrid(entry.getWord(), row, col, direction) ||
                                wordOverlapsExisting(entry.getWord(), row, col, direction, existingWord, i)) {
                            continue;
                        }

                        // Place the word
                        Word newWord = new Word(entry.getWord(), entry.getClue(), row, col, direction, wordNumber);
                        words.add(newWord);
                        placeWordOnGrid(newWord);
                        usedWords.add(entry.getWord());

                        // Recursive call to place next word
                        if (backtrackPlaceWords(wordNumber + 1, depth + 1)) {
                            return true; // This path worked!
                        }

                        // Backtrack - remove the word and try something else
                        words.remove(words.size() - 1);
                        usedWords.remove(entry.getWord());
                        removeWordFromGrid(newWord);
                    }
                }
            }
        }

        return false; // No solution found along this path
    }

    /**
     * Removes a word from the grid.
     * Careful to only remove characters that aren't part of other words.
     */
    private void removeWordFromGrid(Word word) {
        String text = word.getWord();
        int row = word.getRow();
        int col = word.getColumn();

        for (int i = 0; i < text.length(); i++) {
            int currentRow = (word.getDirection() == Word.Direction.ACROSS) ? row : row + i;
            int currentCol = (word.getDirection() == Word.Direction.ACROSS) ? col + i : col;

            // Check if this cell is part of another word
            boolean isShared = false;
            for (Word otherWord : words) {
                if (otherWord != word && isPositionInWord(currentRow, currentCol, otherWord)) {
                    isShared = true;
                    break;
                }
            }

            // Only clear the cell if it's not part of another word
            if (!isShared) {
                grid.setCell(currentRow, currentCol, ' ');
            }
        }
    }
}
