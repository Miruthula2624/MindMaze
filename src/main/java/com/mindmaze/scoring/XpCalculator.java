package com.mindmaze.scoring;

import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.strategy.EvaluationResult;

/**
 * Pure Java XP and progression engine.
 * Computes XP yields and quadratic level progression: Total XP required for level L = 100 * L^2 - 100 * L.
 */
public class XpCalculator {

    public static final int DAILY_CHALLENGE_BONUS_XP = 50;
    public static final int EFFORT_XP_ON_FAILURE = 5;
    public static final long MIN_TIME_FOR_EFFORT_XP_MS = 5000; // Must spend >= 5s to earn effort XP

    public XpBreakdown calculateXp(EvaluationResult result, boolean isDailyChallenge) {
        if (result == null) {
            throw new ValidationException("EvaluationResult cannot be null for XP calculation.");
        }

        int baseXp = 0;
        int effortXp = 0;
        int dailyBonusXp = isDailyChallenge ? DAILY_CHALLENGE_BONUS_XP : 0;

        if (result.isCorrect()) {
            baseXp = getBaseXpForTier(result.difficultyTier());
        } else {
            // Reward persistence if user spent at least 5 seconds trying
            if (result.timeSpentMs() >= MIN_TIME_FOR_EFFORT_XP_MS) {
                effortXp = EFFORT_XP_ON_FAILURE;
            }
        }

        int totalAwarded = baseXp + effortXp + dailyBonusXp;
        return new XpBreakdown(baseXp, effortXp, dailyBonusXp, totalAwarded);
    }

    /**
     * Base XP table per difficulty tier for correct solutions:
     * Tier 1: 20 XP
     * Tier 2: 30 XP
     * Tier 3: 45 XP
     * Tier 4: 65 XP
     * Tier 5: 90 XP
     */
    public int getBaseXpForTier(DifficultyTier tier) {
        if (tier == null) {
            return 20;
        }
        return switch (tier) {
            case TIER_1 -> 20;
            case TIER_2 -> 30;
            case TIER_3 -> 45;
            case TIER_4 -> 65;
            case TIER_5 -> 90;
        };
    }

    /**
     * Calculates cumulative XP threshold required to reach level L:
     * Formula: 100 * L^2 - 100 * L
     * Level 1: 0 XP
     * Level 2: 200 XP
     * Level 3: 600 XP
     * Level 4: 1200 XP
     * Level 5: 2000 XP
     */
    public long calculateXpThresholdForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return 100L * level * level - 100L * level;
    }

    /**
     * Determines user's level based on cumulative total XP.
     */
    public int calculateLevelForTotalXp(long totalXp) {
        if (totalXp <= 0) {
            return 1;
        }

        int level = 1;
        while (calculateXpThresholdForLevel(level + 1) <= totalXp) {
            level++;
        }
        return level;
    }

    /**
     * Checks if an XP addition causes the user to level up.
     */
    public boolean isLevelUp(long previousTotalXp, long newTotalXp) {
        int previousLevel = calculateLevelForTotalXp(previousTotalXp);
        int newLevel = calculateLevelForTotalXp(newTotalXp);
        return newLevel > previousLevel;
    }
}
