package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryPuzzleStrategyTest {

    private MemoryPuzzleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MemoryPuzzleStrategy();
    }

    @Test
    @DisplayName("Should validate ordered sequence recall")
    void shouldValidateOrderedSequence() {
        Puzzle puzzle = new Puzzle(3L, 30L, PuzzleType.MEMORY, DifficultyTier.TIER_3,
                "Digit Span", "Recall sequence", 25, 220,
                "{}", "{\"ordered\": true, \"sequence\": [\"3\", \"7\", \"9\"]}");

        assertThat(strategy.validateAnswer(puzzle, "{\"sequence\": [\"3\", \"7\", \"9\"]}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"sequence\": [\"7\", \"3\", \"9\"]}")).isFalse();
    }

    @Test
    @DisplayName("Should validate unordered grid tile recall")
    void shouldValidateUnorderedGridTiles() {
        Puzzle puzzle = new Puzzle(4L, 30L, PuzzleType.MEMORY, DifficultyTier.TIER_2,
                "Grid Memory", "Recall lit tiles", 30, 150,
                "{}", "{\"ordered\": false, \"tiles\": [\"1,1\", \"2,3\"]}");

        // In reverse order, but unordered match should pass
        assertThat(strategy.validateAnswer(puzzle, "{\"tiles\": [\"2,3\", \"1,1\"]}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"tiles\": [\"2,3\", \"3,3\"]}")).isFalse();
    }
}
