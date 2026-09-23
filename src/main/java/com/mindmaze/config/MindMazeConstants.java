package com.mindmaze.config;

/**
 * Global pure-Java domain constants for MindMaze.
 */
public final class MindMazeConstants {

    private MindMazeConstants() {
        // Utility constant class
    }

    public static final String APP_NAME = "MindMaze";
    public static final String APP_VERSION = "0.1.0-SNAPSHOT";

    // Timing constants
    public static final long MIN_SOLVE_TIME_MS = 500;
    public static final long MAX_SOLVE_TIME_MS = 600_000; // 10 minutes

    // Scoring & XP constants
    public static final double MAX_TIME_BONUS_MULTIPLIER = 0.50;
    public static final int DAILY_CHALLENGE_XP = 50;
    public static final int EFFORT_XP = 5;
}
