package com.mindmaze.puzzle.domain;

/**
 * Fundamental cognitive puzzle categories supported by MindMaze.
 */
public enum PuzzleType {
    NUMBER("Number Reasoning", "Mental arithmetic, numerical sequences, and algebraic balance"),
    LOGIC("Logical Deduction", "Deductive riddles, condition testing, and truth puzzles"),
    MEMORY("Working Memory", "Sequential pattern recall, visual grid memory, and spatial spans"),
    WORD("Verbal Fluency", "Anagram resolution, word association, and lexical puzzles"),
    PATTERN("Pattern Recognition", "Abstract matrix completion, symmetry, and visual sequences");

    private final String displayName;
    private final String description;

    PuzzleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
