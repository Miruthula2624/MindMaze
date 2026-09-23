package com.mindmaze.common.exception;

import com.mindmaze.puzzle.domain.PuzzleType;

/**
 * Thrown when no strategy has been registered for a requested puzzle type.
 */
public class StrategyNotFoundException extends MindMazeException {

    private final PuzzleType puzzleType;

    public StrategyNotFoundException(PuzzleType puzzleType) {
        super("No PuzzleStrategy registered for puzzle type: " + puzzleType);
        this.puzzleType = puzzleType;
    }

    public PuzzleType getPuzzleType() {
        return puzzleType;
    }
}
