package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogicPuzzleStrategyTest {

    private LogicPuzzleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new LogicPuzzleStrategy();
    }

    @Test
    @DisplayName("Should validate logic answer case-insensitively")
    void shouldValidateLogicAnswerCaseInsensitively() {
        Puzzle puzzle = new Puzzle(2L, 20L, PuzzleType.LOGIC, DifficultyTier.TIER_2,
                "Riddle", "Who is telling truth?", 30, 150,
                "{\"prompt\": \"A says B lies...\"}", "{\"correctOption\": \"B\"}");

        assertThat(strategy.validateAnswer(puzzle, "{\"selectedOption\": \"b\"}")).isTrue();
        assertThat(strategy.validateAnswer(puzzle, "{\"selectedOption\": \"A\"}")).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify supported type as LOGIC")
    void shouldReturnCorrectSupportedType() {
        assertThat(strategy.getSupportedType()).isEqualTo(PuzzleType.LOGIC);
    }
}
