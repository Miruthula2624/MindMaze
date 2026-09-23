package com.mindmaze.difficulty;

import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.strategy.EvaluationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveDifficultyEngineTest {

    private AdaptiveDifficultyEngine engine;
    private UserCategoryPerformance performance;

    @BeforeEach
    void setUp() {
        engine = new AdaptiveDifficultyEngine();
        performance = new UserCategoryPerformance(1L, 100L, 1L); // starts at TIER_1, 0 attempts
    }

    @Test
    @DisplayName("Cold-Start: User with < 3 attempts is NEVER demoted on an incorrect attempt (grace period)")
    void shouldNeverDemoteDuringColdStartGracePeriod() {
        EvaluationResult failResult = EvaluationResult.incorrect(
                15000, 40, DifficultyTier.TIER_1, 1L, "Failed", "{}"
        );

        TierAdjustmentDecision decision1 = engine.evaluateAndAdjustTier(performance, failResult);
        assertThat(decision1.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_1);

        TierAdjustmentDecision decision2 = engine.evaluateAndAdjustTier(performance, failResult);
        assertThat(decision2.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);
        assertThat(decision2.reason()).contains("grace period");
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_1);
    }

    @Test
    @DisplayName("Cold-Start: 2 consecutive fast correct answers promote from Tier 1 to Tier 2")
    void shouldRapidlyPromoteDuringColdStartWithFastSolves() {
        // Fast solve (10s on 40s target -> 25% time ratio <= 80%)
        EvaluationResult fastSolve = EvaluationResult.correct(
                10000, 40, DifficultyTier.TIER_1, 1L, "Great", "{}"
        );

        TierAdjustmentDecision decision1 = engine.evaluateAndAdjustTier(performance, fastSolve);
        assertThat(decision1.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED); // 1st attempt

        TierAdjustmentDecision decision2 = engine.evaluateAndAdjustTier(performance, fastSolve);
        assertThat(decision2.action()).isEqualTo(TierAdjustmentDecision.Action.PROMOTED);
        assertThat(decision2.newTier()).isEqualTo(DifficultyTier.TIER_2);
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_2);
    }

    @Test
    @DisplayName("Established User: 3 consecutive fast correct answers promote tier")
    void shouldPromoteEstablishedUserAfterThreeFastWins() {
        // Elevate to established user status with 3 prior attempts (consecutiveCorrect starts at 0)
        performance.recordAttempt(true, 15000);
        performance.recordAttempt(true, 15000);
        performance.recordAttempt(false, 15000); // consecutiveCorrect reset to 0
        performance.setCurrentTier(DifficultyTier.TIER_2);

        EvaluationResult win1 = EvaluationResult.correct(15000, 30, DifficultyTier.TIER_2, 1L, "Win", "{}");
        EvaluationResult win2 = EvaluationResult.correct(15000, 30, DifficultyTier.TIER_2, 1L, "Win", "{}");
        EvaluationResult win3 = EvaluationResult.correct(15000, 30, DifficultyTier.TIER_2, 1L, "Win", "{}");

        engine.evaluateAndAdjustTier(performance, win1);
        engine.evaluateAndAdjustTier(performance, win2);
        TierAdjustmentDecision decision3 = engine.evaluateAndAdjustTier(performance, win3);

        assertThat(decision3.action()).isEqualTo(TierAdjustmentDecision.Action.PROMOTED);
        assertThat(decision3.newTier()).isEqualTo(DifficultyTier.TIER_3);
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_3);
    }

    @Test
    @DisplayName("Established User: 2 consecutive failures demote tier")
    void shouldDemoteEstablishedUserAfterTwoFailures() {
        // Set user at TIER_3 with established history
        performance.setCurrentTier(DifficultyTier.TIER_3);
        performance.recordAttempt(true, 10000);
        performance.recordAttempt(true, 10000);
        performance.recordAttempt(true, 10000);

        EvaluationResult failResult = EvaluationResult.incorrect(
                20000, 25, DifficultyTier.TIER_3, 1L, "Fail", "{}"
        );

        TierAdjustmentDecision d1 = engine.evaluateAndAdjustTier(performance, failResult);
        assertThat(d1.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);

        TierAdjustmentDecision d2 = engine.evaluateAndAdjustTier(performance, failResult);
        assertThat(d2.action()).isEqualTo(TierAdjustmentDecision.Action.DEMOTED);
        assertThat(d2.newTier()).isEqualTo(DifficultyTier.TIER_2);
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_2);
    }

    @Test
    @DisplayName("Clamping: Tier 5 cannot be promoted further")
    void shouldClampAtTier5() {
        performance.setCurrentTier(DifficultyTier.TIER_5);
        performance.recordAttempt(true, 5000);
        performance.recordAttempt(true, 5000);
        performance.recordAttempt(true, 5000);

        EvaluationResult fastWin = EvaluationResult.correct(5000, 15, DifficultyTier.TIER_5, 1L, "Win", "{}");
        engine.evaluateAndAdjustTier(performance, fastWin);
        engine.evaluateAndAdjustTier(performance, fastWin);
        TierAdjustmentDecision decision = engine.evaluateAndAdjustTier(performance, fastWin);

        assertThat(decision.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);
        assertThat(decision.reason()).contains("Maximum difficulty tier");
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_5);
    }

    @Test
    @DisplayName("Flooring: Tier 1 cannot be demoted further")
    void shouldFloorAtTier1() {
        performance.setCurrentTier(DifficultyTier.TIER_1);
        performance.recordAttempt(true, 5000);
        performance.recordAttempt(false, 5000);
        performance.recordAttempt(false, 5000);

        EvaluationResult fail = EvaluationResult.incorrect(10000, 40, DifficultyTier.TIER_1, 1L, "Fail", "{}");
        engine.evaluateAndAdjustTier(performance, fail);
        TierAdjustmentDecision decision = engine.evaluateAndAdjustTier(performance, fail);

        assertThat(decision.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);
        assertThat(decision.reason()).contains("Minimum difficulty tier");
        assertThat(performance.getCurrentTier()).isEqualTo(DifficultyTier.TIER_1);
    }
}
