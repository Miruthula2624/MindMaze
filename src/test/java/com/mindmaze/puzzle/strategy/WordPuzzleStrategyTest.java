package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordPuzzleStrategyTest {

    private WordPuzzleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new WordPuzzleStrategy();
    }

    @Test
    @DisplayName("Should validate anagram resolution ignoring case and trimming whitespace")
    void shouldValidateAnagramCorrectly() {
        Puzzle puzzle = new Puzzle(5L, 40L, PuzzleType.WORD, DifficultyTier.TIER_1,
                "Anagram", "Unscramble: L E P A P", 40, 100,
                "{\"scrambled\": \"LEPAP\"}", "{\"targetWord\": \"APPLE\"}");

        assertThat(strategy.validateAnswer(puzzle, "{\"word\": \" apple \"}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"word\": \"APPLY\"}")).isFalse();
    }

    @Test
    @DisplayName("Should accept valid alternate words from acceptedWords array")
    void shouldAcceptAlternateValidWords() {
        Puzzle puzzle = new Puzzle(6L, 40L, PuzzleType.WORD, DifficultyTier.TIER_2,
                "Anagram", "Unscramble", 30, 150,
                "{\"scrambled\": \"ACT\"}", "{\"targetWord\": \"CAT\", \"acceptedWords\": [\"CAT\", \"ACT\"]}");

        assertThat(strategy.validateAnswer(puzzle, "{\"word\": \"act\"}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"word\": \"cat\"}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"word\": \"tac\"}")).isFalse();
    }
}
