package crosswordjava.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a dictionary of words and clues for the crossword puzzle.
 */
public class Dictionary {
    private List<WordEntry> words;
    private Random random;

    /**
     * Creates a new dictionary with predefined words and clues.
     */
    public Dictionary() {
        words = new ArrayList<>();
        random = new Random();

        // Core programming words with good intersection potential (lots of vowels and
        // common consonants)
        addWord("JAVA", "A programming language");
        addWord("CODE", "Instructions for a computer");
        addWord("ARRAY", "An ordered collection of elements");
        addWord("LOOP", "A control structure for repetition");
        addWord("STRING", "A sequence of characters");
        addWord("INTEGER", "A whole number data type");
        addWord("BOOLEAN", "A true/false data type");
        addWord("METHOD", "A function defined in a class");
        addWord("OBJECT", "Instance of a class");
        addWord("CLASS", "A blueprint for creating objects");
        addWord("STATIC", "A non-instance modifier");
        addWord("VOID", "A return type that returns nothing");
        addWord("PUBLIC", "An access modifier");
        addWord("PRIVATE", "An access modifier for class-only access");
        addWord("EXTENDS", "Inheritance keyword");
        addWord("IMPORT", "Includes external classes");
        addWord("RETURN", "Exits a method with a value");
        addWord("FLOAT", "A decimal number data type");
        addWord("DOUBLE", "A decimal number data type with extended precision");
        addWord("CHAR", "A single character data type");
        addWord("FINAL", "A constant modifier");
        addWord("ABSTRACT", "Cannot be instantiated");
        addWord("INTERFACE", "A contract for classes");
        addWord("ENUM", "A set of constants");
        addWord("PACKAGE", "A namespace for organizing classes");
        addWord("TRY", "Encloses code that might throw an exception");
        addWord("CATCH", "Handles exceptions from a try block");
        addWord("THROW", "Generates an exception");
        addWord("ERROR", "A serious problem");
        addWord("SWITCH", "A multi-way branch statement");
        addWord("CASE", "A branch in a switch statement");
        addWord("BREAK", "Exits a loop or switch statement");
        addWord("DEFAULT", "The fallback in a switch statement");
        addWord("CONTINUE", "Skips to the next iteration");
        addWord("ASSERT", "Tests a condition");
        addWord("STREAM", "Sequence of elements in Java");
        addWord("LAMBDA", "Anonymous function expression");
        addWord("THREAD", "Unit of execution in programming");
        addWord("SETTER", "Method that sets a value");
        addWord("GETTER", "Method that returns a value");

        // Add some shorter words with common letters for good connections
        addWord("API", "Application programming interface");
        addWord("APP", "Software for a specific purpose");
        addWord("BIT", "The smallest unit of data");
        addWord("BUG", "An error in code");
        addWord("CPU", "Central processing unit");
        addWord("DOM", "Document object model");
        addWord("FOR", "A type of loop statement");
        addWord("OOP", "Object-oriented programming");
        addWord("RAM", "Random access memory");
        addWord("SET", "Collection with no duplicates");
        addWord("SQL", "Database query language");
        addWord("SUM", "Total of values");
        addWord("URL", "Web address");
        addWord("USE", "Consumption of a resource");
        addWord("VAR", "Variable declaration keyword");
        addWord("XML", "Extensible markup language");
    }

    /**
     * Adds a word and its clue to the dictionary.
     * 
     * @param word The word to add
     * @param clue The clue for the word
     */
    public void addWord(String word, String clue) {
        words.add(new WordEntry(word.toUpperCase(), clue));
    }

    /**
     * Gets a random word from the dictionary.
     * 
     * @param minLength Minimum length of the word
     * @param maxLength Maximum length of the word
     * @return A random word entry, or null if no word matches the criteria
     */
    public WordEntry getRandomWord(int minLength, int maxLength) {
        List<WordEntry> matchingWords = new ArrayList<>();

        for (WordEntry entry : words) {
            int length = entry.getWord().length();
            if (length >= minLength && length <= maxLength) {
                matchingWords.add(entry);
            }
        }

        if (matchingWords.isEmpty()) {
            return null;
        }

        int index = random.nextInt(matchingWords.size());
        return matchingWords.get(index);
    }

    /**
     * Gets all words that contain the specified character at the specified
     * position.
     * 
     * @param c        The character that must be in the word
     * @param position The position where the character must be
     * @return A list of word entries that match the criteria
     */
    public List<WordEntry> getAllWordsWithCharAt(char c, int position) {
        List<WordEntry> matchingWords = new ArrayList<>();

        for (WordEntry entry : words) {
            String word = entry.getWord();
            int length = word.length();
            if (position < length && word.charAt(position) == Character.toUpperCase(c)) {
                matchingWords.add(entry);
            }
        }

        return matchingWords;
    }

    /**
     * Gets all words in the dictionary.
     * 
     * @return A list of all word entries
     */
    public List<WordEntry> getAllWords() {
        return new ArrayList<>(words);
    }

    /**
     * Represents a word and its clue in the dictionary.
     */
    public static class WordEntry {
        private String word;
        private String clue;

        /**
         * Creates a new word entry.
         * 
         * @param word The word
         * @param clue The clue for the word
         */
        public WordEntry(String word, String clue) {
            this.word = word;
            this.clue = clue;
        }

        /**
         * Gets the word.
         * 
         * @return The word
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
    }
}
