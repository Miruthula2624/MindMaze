package com.mindmaze.puzzle.strategy;

import com.mindmaze.common.exception.InvalidAttemptException;
import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberPuzzleStrategyTest {

    private NumberPuzzleStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new NumberPuzzleStrategy();
    }

    @Test
    @DisplayName("Should validate correct number answer when values match exactly")
    void shouldValidateCorrectNumberAnswer() {
        Puzzle puzzle = new Puzzle(1L, 10L, PuzzleType.NUMBER, DifficultyTier.TIER_2,
                "Next Number", "Find next in sequence: 2, 4, 8, ?", 30, 150,
                "{\"sequence\": [2, 4, 8]}", "{\"expectedValue\": 16}");

        boolean isCorrect = strategy.validateAnswer(puzzle, "{\"submittedValue\": 16}");
        assertThat(isCorrect).isTrue();
    }

    @Test
    @DisplayName("Should return false when submitted number is incorrect")
    void shouldReturnFalseForIncorrectNumberAnswer() {
        Puzzle puzzle = new Puzzle(1L, 10L, PuzzleType.NUMBER, DifficultyTier.TIER_2,
                "Next Number", "Find next in sequence", 30, 150,
                "{\"sequence\": [2, 4, 8]}", "{\"expectedValue\": 16}");

        boolean isCorrect = strategy.validateAnswer(puzzle, "{\"submittedValue\": 14}");
        assertThat(isCorrect).isFalse();
    }

    @Test
    @DisplayName("Should evaluate and generate correct EvaluationResult for fast solve")
    void shouldEvaluateFastSolve() {
        Puzzle puzzle = new Puzzle(1L, 10L, PuzzleType.NUMBER, DifficultyTier.TIER_3,
                "Math Puzzle", "Solve equation", 25, 220,
                "{\"equation\": \"7 * 8\"}", "{\"expectedValue\": 56}");

        EvaluationResult result = strategy.evaluate(puzzle, "{\"submittedValue\": 56}", 8000);

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.timeSpentMs()).isEqualTo(8000);
        assertThat(result.targetTimeSeconds()).isEqualTo(25);
        assertThat(result.difficultyTier()).isEqualTo(DifficultyTier.TIER_3);
        assertThat(result.feedbackMessage()).contains("Exceptional solve");
    }

    @Test
    @DisplayName("Should throw ValidationException when time spent is under 500ms")
    void shouldRejectSub500msSolve() {
        Puzzle puzzle = new Puzzle(1L, 10L, PuzzleType.NUMBER, DifficultyTier.TIER_1,
                "Quick Math", "1 + 1", 40, 100,
                "{}", "{\"expectedValue\": 2}");

        assertThatThrownBy(() -> strategy.evaluate(puzzle, "{\"submittedValue\": 2}", 300))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("at least 500ms");
    }

    @Test
    @DisplayName("Should throw InvalidAttemptException when puzzle is inactive")
    void shouldRejectInactivePuzzle() {
        Puzzle puzzle = new Puzzle(1L, 10L, PuzzleType.NUMBER, DifficultyTier.TIER_1,
                "Quick Math", "1 + 1", 40, 100,
                "{}", "{\"expectedValue\": 2}");
        puzzle.setActive(false);

        assertThatThrownBy(() -> strategy.evaluate(puzzle, "{\"submittedValue\": 2}", 1500))
                .isInstanceOf(InvalidAttemptException.class)
                .hasMessageContaining("inactive puzzle");
    }
}
