package com.mindmaze.difficulty;

import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.strategy.EvaluationResult;

/**
 * Pure Java Adaptive Difficulty Engine.
 * Dynamically evaluates category performance and determines tier adjustments
 * with robust cold-start protection for new users.
 */
public class AdaptiveDifficultyEngine {

    public static final int COLD_START_MIN_ATTEMPTS = 3;
    public static final int PROMOTION_CONSECUTIVE_CORRECT_REQUIRED = 3;
    public static final int DEMOTION_CONSECUTIVE_FAILURES_REQUIRED = 2;
    public static final double DEMOTION_ROLLING_ACCURACY_THRESHOLD = 0.40;
    public static final double PROMOTION_MAX_TIME_RATIO = 0.90; // Must solve within 90% of target time

    /**
     * Evaluates user attempt and applies adaptive tier adjustment to UserCategoryPerformance.
     *
     * @param performance mutable category performance state
     * @param evaluationResult outcome of the latest puzzle attempt
     * @return decision explaining whether tier was promoted, maintained, or demoted
     */
    public TierAdjustmentDecision evaluateAndAdjustTier(UserCategoryPerformance performance,
                                                        EvaluationResult evaluationResult) {
        if (performance == null || evaluationResult == null) {
            throw new ValidationException("Performance state and EvaluationResult cannot be null.");
        }

        // 1. Record the attempt outcome
        performance.recordAttempt(evaluationResult.isCorrect(), evaluationResult.timeSpentMs());

        DifficultyTier previousTier = performance.getCurrentTier();
        double timeRatio = (double) evaluationResult.timeSpentMs() / (evaluationResult.targetTimeSeconds() * 1000.0);

        // 2. Cold-Start Handling (< 3 attempts in category)
        if (performance.getTotalAttempts() < COLD_START_MIN_ATTEMPTS) {
            return handleColdStart(performance, previousTier, evaluationResult.isCorrect(), timeRatio);
        }

        // 3. Established User Handling (>= 3 attempts in category)
        return handleEstablishedUser(performance, previousTier, evaluationResult.isCorrect(), timeRatio);
    }

    private TierAdjustmentDecision handleColdStart(UserCategoryPerformance performance,
                                                   DifficultyTier previousTier,
                                                   boolean isCorrect,
                                                   double timeRatio) {
        // Safe cold-start: an incorrect attempt NEVER causes a demotion
        if (!isCorrect) {
            return TierAdjustmentDecision.maintained(
                    previousTier,
                    "Cold-start grace period: early incorrect attempt does not trigger demotion."
            );
        }

        // Fast early promotion: 2 consecutive fast correct answers promote from Tier 1 to Tier 2
        if (previousTier == DifficultyTier.TIER_1 && performance.getConsecutiveCorrect() >= 2 && timeRatio <= 0.80) {
            performance.promoteTier();
            return TierAdjustmentDecision.promoted(
                    previousTier,
                    performance.getCurrentTier(),
                    "Rapid cold-start calibration: 2 consecutive fast correct answers in Tier 1."
            );
        }

        return TierAdjustmentDecision.maintained(
                previousTier,
                "Cold-start assessment ongoing (insufficient historical data for general adjustment)."
        );
    }

    private TierAdjustmentDecision handleEstablishedUser(UserCategoryPerformance performance,
                                                         DifficultyTier previousTier,
                                                         boolean isCorrect,
                                                         double timeRatio) {
        // Check for promotion eligibility
        if (isCorrect && performance.getConsecutiveCorrect() >= PROMOTION_CONSECUTIVE_CORRECT_REQUIRED
                && timeRatio <= PROMOTION_MAX_TIME_RATIO) {
            if (previousTier.isMax()) {
                return TierAdjustmentDecision.maintained(
                        previousTier,
                        "Maximum difficulty tier (Grandmaster) reached."
                );
            }
            performance.promoteTier();
            return TierAdjustmentDecision.promoted(
                    previousTier,
                    performance.getCurrentTier(),
                    "Promoted: Achieved " + PROMOTION_CONSECUTIVE_CORRECT_REQUIRED
                            + " consecutive fast correct solves at " + previousTier.getTitle() + "."
            );
        }

        // Check for demotion eligibility
        boolean consecutiveFailuresExceeded = performance.getConsecutiveFailures() >= DEMOTION_CONSECUTIVE_FAILURES_REQUIRED;
        boolean rollingAccuracyDropped = performance.getRollingAccuracy() < DEMOTION_ROLLING_ACCURACY_THRESHOLD
                && performance.getRecentOutcomes().size() >= 3;

        if (consecutiveFailuresExceeded || rollingAccuracyDropped) {
            if (previousTier.isMin()) {
                return TierAdjustmentDecision.maintained(
                        previousTier,
                        "Minimum difficulty tier (Novice) reached."
                );
            }
            String reason = consecutiveFailuresExceeded
                    ? "Demoted: " + DEMOTION_CONSECUTIVE_FAILURES_REQUIRED + " consecutive incorrect attempts."
                    : "Demoted: Rolling accuracy dropped below " + (int)(DEMOTION_ROLLING_ACCURACY_THRESHOLD * 100) + "%.";
            performance.demoteTier();
            return TierAdjustmentDecision.demoted(previousTier, performance.getCurrentTier(), reason);
        }

        // Performance is stable; maintain tier
        return TierAdjustmentDecision.maintained(
                previousTier,
                "Stable performance maintained at " + previousTier.getTitle() + "."
        );
    }
}
