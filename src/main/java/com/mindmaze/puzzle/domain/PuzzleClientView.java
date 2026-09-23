package com.mindmaze.puzzle.domain;

/**
 * Immutable view of a puzzle designed strictly for safe client presentation.
 * Note: Never includes solutionDataJson, preserving server-side solution confidentiality.
 */
public record PuzzleClientView(
        Long id,
        Long categoryId,
        PuzzleType puzzleType,
        DifficultyTier difficultyTier,
        String title,
        String instruction,
        int targetTimeSeconds,
        int basePoints,
        String puzzleDataJson
) {
}
