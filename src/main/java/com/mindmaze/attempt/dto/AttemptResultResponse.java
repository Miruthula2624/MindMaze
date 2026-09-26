package com.mindmaze.attempt.dto;

import com.mindmaze.puzzle.domain.DifficultyTier;

/**
 * Typed response DTO delivering evaluation results, score bonuses, XP gains,
 * and adaptive difficulty adjustments back to the client.
 */
public record AttemptResultResponse(
        boolean isCorrect,
        int scoreEarned,
        int xpEarned,
        long timeSpentMs,
        DifficultyTier currentTier,
        DifficultyTier newTier,
        String tierAction,
        String tierReason,
        String feedbackMessage
) {
}
