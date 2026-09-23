package com.mindmaze.scoring;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.strategy.EvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XpCalculatorTest {

    private XpCalculator xpCalculator;

    @BeforeEach
    void setUp() {
        xpCalculator = new XpCalculator();
    }

    @Test
    @DisplayName("Should award tier-based base XP for correct solves")
    void shouldAwardBaseXpPerTier() {
        assertThat(xpCalculator.getBaseXpForTier(DifficultyTier.TIER_1)).isEqualTo(20);
        assertThat(xpCalculator.getBaseXpForTier(DifficultyTier.TIER_2)).isEqualTo(30);
        assertThat(xpCalculator.getBaseXpForTier(DifficultyTier.TIER_3)).isEqualTo(45);
        assertThat(xpCalculator.getBaseXpForTier(DifficultyTier.TIER_4)).isEqualTo(65);
        assertThat(xpCalculator.getBaseXpForTier(DifficultyTier.TIER_5)).isEqualTo(90);
    }

    @Test
    @DisplayName("Should award 5 effort XP on failure if user tried for at least 5 seconds")
    void shouldAwardEffortXpForLongAttempt() {
        EvaluationResult failedLong = EvaluationResult.incorrect(
                6000, 30, DifficultyTier.TIER_2, 1L, "Failed", "{}"
        );
        XpBreakdown breakdown = xpCalculator.calculateXp(failedLong, false);
        assertThat(breakdown.baseXp()).isEqualTo(0);
        assertThat(breakdown.effortXp()).isEqualTo(5);
        assertThat(breakdown.totalXpAwarded()).isEqualTo(5);

        // Under 5s failure gets 0 XP
        EvaluationResult failedQuick = EvaluationResult.incorrect(
                2000, 30, DifficultyTier.TIER_2, 1L, "Failed", "{}"
        );
        XpBreakdown breakdownQuick = xpCalculator.calculateXp(failedQuick, false);
        assertThat(breakdownQuick.effortXp()).isEqualTo(0);
        assertThat(breakdownQuick.totalXpAwarded()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should award 50 bonus XP for Daily Challenge completion")
    void shouldAwardDailyChallengeBonusXp() {
        EvaluationResult correctSolve = EvaluationResult.correct(
                12000, 30, DifficultyTier.TIER_2, 1L, "Solved", "{}"
        );
        XpBreakdown breakdown = xpCalculator.calculateXp(correctSolve, true);

        assertThat(breakdown.baseXp()).isEqualTo(30);
        assertThat(breakdown.dailyBonusXp()).isEqualTo(50);
        assertThat(breakdown.totalXpAwarded()).isEqualTo(80);
    }

    @Test
    @DisplayName("Should accurately calculate level thresholds using 100 * L^2 - 100 * L formula")
    void shouldCalculateAccurateLevelThresholds() {
        assertThat(xpCalculator.calculateXpThresholdForLevel(1)).isEqualTo(0);
        assertThat(xpCalculator.calculateXpThresholdForLevel(2)).isEqualTo(200);
        assertThat(xpCalculator.calculateXpThresholdForLevel(3)).isEqualTo(600);
        assertThat(xpCalculator.calculateXpThresholdForLevel(4)).isEqualTo(1200);
        assertThat(xpCalculator.calculateXpThresholdForLevel(5)).isEqualTo(2000);
    }

    @Test
    @DisplayName("Should determine user level accurately from cumulative total XP")
    void shouldDetermineUserLevelFromTotalXp() {
        assertThat(xpCalculator.calculateLevelForTotalXp(0)).isEqualTo(1);
        assertThat(xpCalculator.calculateLevelForTotalXp(199)).isEqualTo(1);
        assertThat(xpCalculator.calculateLevelForTotalXp(200)).isEqualTo(2);
        assertThat(xpCalculator.calculateLevelForTotalXp(599)).isEqualTo(2);
        assertThat(xpCalculator.calculateLevelForTotalXp(600)).isEqualTo(3);
        assertThat(xpCalculator.calculateLevelForTotalXp(1200)).isEqualTo(4);
    }

    @Test
    @DisplayName("Should detect level-up event when total XP crosses threshold")
    void shouldDetectLevelUp() {
        assertThat(xpCalculator.isLevelUp(180, 210)).isTrue();
        assertThat(xpCalculator.isLevelUp(150, 180)).isFalse();
    }
}
