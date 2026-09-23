package com.mindmaze.scoring;

import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.strategy.EvaluationResult;

/**
 * Pure Java scoring engine implementing multi-factor cognitive score calculations:
 * Final Score = floor(BasePoints * (1 + TimeBonus) * StreakMultiplier) if correct, else 0.
 */
public class ScoreCalculator {

    public ScoreBreakdown calculateScore(EvaluationResult evaluationResult, int sessionConsecutiveStreak) {
        if (evaluationResult == null) {
            throw new ValidationException("EvaluationResult cannot be null for score calculation.");
        }

        int basePoints = evaluationResult.difficultyTier().getBasePoints();

        // An incorrect answer receives 0 points
        if (!evaluationResult.isCorrect()) {
            return ScoreBreakdown.zero(basePoints);
        }

        // Time bonus calculation
        double timeBonusMultiplier = calculateTimeBonusMultiplier(
                evaluationResult.timeSpentMs(),
                evaluationResult.targetTimeSeconds()
        );

        // Session streak multiplier calculation
        double streakMultiplier = calculateStreakMultiplier(sessionConsecutiveStreak);

        // Final score equation: floor(basePoints * (1 + timeBonus) * streakMultiplier)
        double totalRawScore = basePoints * (1.0 + timeBonusMultiplier) * streakMultiplier;
        int finalScore = (int) Math.floor(totalRawScore);

        return new ScoreBreakdown(basePoints, timeBonusMultiplier, streakMultiplier, finalScore);
    }

    /**
     * Computes linear time bonus multiplier:
     * - <= 50% target time: full 50% (+0.50) bonus
     * - > 50% and <= 100% target time: linear decay from 0.50 down to 0.00
     * - > 100% target time: 0.00 bonus
     */
    public double calculateTimeBonusMultiplier(long timeSpentMs, int targetTimeSeconds) {
        if (targetTimeSeconds <= 0) {
            return 0.0;
        }

        double timeSpentSeconds = timeSpentMs / 1000.0;
        double halfTarget = targetTimeSeconds / 2.0;

        if (timeSpentSeconds <= halfTarget) {
            return 0.50; // Maximum speed bonus
        } else if (timeSpentSeconds <= targetTimeSeconds) {
            // Linear decay from 0.50 down to 0.00
            double remainingRatio = (targetTimeSeconds - timeSpentSeconds) / halfTarget;
            return Math.max(0.0, Math.min(0.50, remainingRatio * 0.50));
        } else {
            return 0.0; // Over target time: 0 bonus
        }
    }

    /**
     * Computes session streak multiplier:
     * - 1-2 consecutive correct: 1.0x (standard)
     * - 3-4 consecutive correct: 1.1x (+10%)
     * - 5+ consecutive correct: 1.25x (+25%)
     */
    public double calculateStreakMultiplier(int sessionConsecutiveStreak) {
        if (sessionConsecutiveStreak >= 5) {
            return 1.25;
        } else if (sessionConsecutiveStreak >= 3) {
            return 1.10;
        } else {
            return 1.0;
        }
    }
}
