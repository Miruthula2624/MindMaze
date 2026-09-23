package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.DifficultyTier;

/**
 * Immutable record representing the pure evaluation outcome of a puzzle submission.
 * Strictly decoupled from score points and XP, which are calculated by dedicated scoring domain engines.
 */
public record EvaluationResult(
        boolean isCorrect,
        long timeSpentMs,
        int targetTimeSeconds,
        DifficultyTier difficultyTier,
        Long categoryId,
        String feedbackMessage,
        String sanitizedSubmission
) {
    public static EvaluationResult correct(long timeSpentMs, int targetTimeSeconds,
                                           DifficultyTier tier, Long categoryId,
                                           String feedback, String submission) {
        return new EvaluationResult(true, timeSpentMs, targetTimeSeconds, tier, categoryId, feedback, submission);
    }

    public static EvaluationResult incorrect(long timeSpentMs, int targetTimeSeconds,
                                             DifficultyTier tier, Long categoryId,
                                             String feedback, String submission) {
        return new EvaluationResult(false, timeSpentMs, targetTimeSeconds, tier, categoryId, feedback, submission);
    }
}
