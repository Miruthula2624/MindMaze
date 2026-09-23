package com.mindmaze.scoring;

/**
 * Immutable record representing the breakdown of experience points awarded for an attempt.
 */
public record XpBreakdown(
        int baseXp,
        int effortXp,
        int dailyBonusXp,
        int totalXpAwarded
) {
}
