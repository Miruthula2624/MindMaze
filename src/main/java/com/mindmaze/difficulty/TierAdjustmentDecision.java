package com.mindmaze.difficulty;

import com.mindmaze.puzzle.domain.DifficultyTier;

/**
 * Immutable record representing the decision reached by the AdaptiveDifficultyEngine.
 */
public record TierAdjustmentDecision(
        DifficultyTier previousTier,
        DifficultyTier newTier,
        Action action,
        String reason
) {
    public enum Action {
        PROMOTED,
        MAINTAINED,
        DEMOTED
    }

    public static TierAdjustmentDecision promoted(DifficultyTier prev, DifficultyTier next, String reason) {
        return new TierAdjustmentDecision(prev, next, Action.PROMOTED, reason);
    }

    public static TierAdjustmentDecision maintained(DifficultyTier current, String reason) {
        return new TierAdjustmentDecision(current, current, Action.MAINTAINED, reason);
    }

    public static TierAdjustmentDecision demoted(DifficultyTier prev, DifficultyTier next, String reason) {
        return new TierAdjustmentDecision(prev, next, Action.DEMOTED, reason);
    }
}
