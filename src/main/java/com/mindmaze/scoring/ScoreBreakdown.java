package com.mindmaze.scoring;

/**
 * Immutable record representing the multi-factor scoring calculation breakdown.
 */
public record ScoreBreakdown(
        int basePoints,
        double timeBonusMultiplier,
        double streakMultiplier,
        int finalScore
) {
    public static ScoreBreakdown zero(int basePoints) {
        return new ScoreBreakdown(basePoints, 0.0, 1.0, 0);
    }
}
