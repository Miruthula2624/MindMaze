package com.mindmaze.puzzle.domain;

/**
 * Standard cognitive difficulty tiers bounded from Tier 1 (Novice) to Tier 5 (Grandmaster).
 */
public enum DifficultyTier {
    TIER_1(1, "Novice", 40, 100),
    TIER_2(2, "Apprentice", 30, 150),
    TIER_3(3, "Adept", 25, 220),
    TIER_4(4, "Master", 20, 300),
    TIER_5(5, "Grandmaster", 15, 400);

    private final int level;
    private final String title;
    private final int defaultTargetTimeSeconds;
    private final int basePoints;

    DifficultyTier(int level, String title, int defaultTargetTimeSeconds, int basePoints) {
        this.level = level;
        this.title = title;
        this.defaultTargetTimeSeconds = defaultTargetTimeSeconds;
        this.basePoints = basePoints;
    }

    public int getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public int getDefaultTargetTimeSeconds() {
        return defaultTargetTimeSeconds;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public boolean isMin() {
        return this == TIER_1;
    }

    public boolean isMax() {
        return this == TIER_5;
    }

    /**
     * Resolves next higher difficulty tier, capped at TIER_5.
     */
    public DifficultyTier nextTier() {
        if (this == TIER_5) {
            return TIER_5;
        }
        return fromLevel(this.level + 1);
    }

    /**
     * Resolves previous lower difficulty tier, floored at TIER_1.
     */
    public DifficultyTier previousTier() {
        if (this == TIER_1) {
            return TIER_1;
        }
        return fromLevel(this.level - 1);
    }

    /**
     * Maps an integer level (1-5) to its corresponding enum constant.
     */
    public static DifficultyTier fromLevel(int level) {
        for (DifficultyTier tier : values()) {
            if (tier.level == level) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty tier level: " + level + ". Must be between 1 and 5.");
    }
}
