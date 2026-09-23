package com.mindmaze.scoring;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.strategy.EvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreCalculatorTest {

    private ScoreCalculator scoreCalculator;

    @BeforeEach
    void setUp() {
        scoreCalculator = new ScoreCalculator();
    }

    @Test
    @DisplayName("Should award 0 points when the answer is incorrect")
    void shouldAwardZeroPointsForIncorrectAnswer() {
        EvaluationResult result = EvaluationResult.incorrect(
                10000, 30, DifficultyTier.TIER_2, 1L, "Wrong", "{}"
        );

        ScoreBreakdown breakdown = scoreCalculator.calculateScore(result, 5);

        assertThat(breakdown.finalScore()).isEqualTo(0);
        assertThat(breakdown.basePoints()).isEqualTo(150);
        assertThat(breakdown.timeBonusMultiplier()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should award full 50% time bonus for fast solve (<= 50% target time)")
    void shouldAwardMaxTimeBonusForFastSolve() {
        // Target time = 40s, Time spent = 15s (<= 20s) -> max 50% bonus
        EvaluationResult result = EvaluationResult.correct(
                15000, 40, DifficultyTier.TIER_1, 1L, "Great", "{}"
        );

        ScoreBreakdown breakdown = scoreCalculator.calculateScore(result, 1);

        assertThat(breakdown.timeBonusMultiplier()).isEqualTo(0.50);
        assertThat(breakdown.streakMultiplier()).isEqualTo(1.0);
        // Base = 100, 100 * (1 + 0.50) * 1.0 = 150
        assertThat(breakdown.finalScore()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should apply linear time bonus decay when time is between 50% and 100% of target")
    void shouldApplyLinearTimeBonusDecay() {
        // Target time = 40s, Time spent = 30s (halfway between 20s and 40s) -> bonus should be 0.25 (25%)
        EvaluationResult result = EvaluationResult.correct(
                30000, 40, DifficultyTier.TIER_1, 1L, "Good", "{}"
        );

        ScoreBreakdown breakdown = scoreCalculator.calculateScore(result, 1);

        assertThat(breakdown.timeBonusMultiplier()).isEqualTo(0.25);
        // Base = 100, 100 * (1 + 0.25) * 1.0 = 125
        assertThat(breakdown.finalScore()).isEqualTo(125);
    }

    @Test
    @DisplayName("Should award 0% time bonus when time spent exceeds target time")
    void shouldAwardZeroTimeBonusOverTargetTime() {
        // Target time = 30s, Time spent = 35s
        EvaluationResult result = EvaluationResult.correct(
                35000, 30, DifficultyTier.TIER_2, 1L, "Solved", "{}"
        );

        ScoreBreakdown breakdown = scoreCalculator.calculateScore(result, 1);

        assertThat(breakdown.timeBonusMultiplier()).isEqualTo(0.0);
        // Base = 150, 150 * 1.0 * 1.0 = 150
        assertThat(breakdown.finalScore()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should apply session streak multipliers correctly (1.10x for 3-4, 1.25x for 5+)")
    void shouldApplyStreakMultipliers() {
        EvaluationResult result = EvaluationResult.correct(
                35000, 30, DifficultyTier.TIER_2, 1L, "Solved", "{}"
        ); // Base = 150, timeBonus = 0

        // Streak = 3 -> 1.10x -> 150 * 1.10 = 165
        ScoreBreakdown breakdown3 = scoreCalculator.calculateScore(result, 3);
        assertThat(breakdown3.streakMultiplier()).isEqualTo(1.10);
        assertThat(breakdown3.finalScore()).isEqualTo(165);

        // Streak = 5 -> 1.25x -> 150 * 1.25 = 187
        ScoreBreakdown breakdown5 = scoreCalculator.calculateScore(result, 5);
        assertThat(breakdown5.streakMultiplier()).isEqualTo(1.25);
        assertThat(breakdown5.finalScore()).isEqualTo(187);
    }
}
