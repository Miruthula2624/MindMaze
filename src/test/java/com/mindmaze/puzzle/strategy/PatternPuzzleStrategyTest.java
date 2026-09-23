package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatternPuzzleStrategyTest {

    private PatternPuzzleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PatternPuzzleStrategy();
    }

    @Test
    @DisplayName("Should validate pattern selection match")
    void shouldValidatePatternSelection() {
        Puzzle puzzle = new Puzzle(7L, 50L, PuzzleType.PATTERN, DifficultyTier.TIER_3,
                "Matrix Reasoning", "Select missing cell", 25, 220,
                "{\"matrix\": [[1, 2], [3, null]]}", "{\"correctPatternId\": \"D\"}");

        assertThat(strategy.validateAnswer(puzzle, "{\"selectedPatternId\": \"d\"}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"selectedPatternId\": \"A\"}")).isFalse();
    }
}
