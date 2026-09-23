package com.mindmaze.puzzle.strategy;

import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

/**
 * Strategy interface for category-specific puzzle answer validation and evaluation.
 */
public interface PuzzleStrategy {

    /**
     * Returns the specific puzzle category type supported by this strategy.
     */
    PuzzleType getSupportedType();

    /**
     * Validates whether the user's submitted answer matches the puzzle's server-side solution.
     *
     * @param puzzle the puzzle domain entity containing server-side solution payload
     * @param userSubmissionJson the JSON payload representing the user's answer
     * @return true if the answer is completely correct; false otherwise
     */
    boolean validateAnswer(Puzzle puzzle, String userSubmissionJson);

    /**
     * Evaluates a user submission and produces an EvaluationResult containing correctness,
     * time context, and feedback.
     *
     * @param puzzle the puzzle domain entity
     * @param userSubmissionJson the JSON payload representing user answer
     * @param timeSpentMs total time elapsed in milliseconds
     * @return EvaluationResult containing outcome without calculating points/XP
     */
    EvaluationResult evaluate(Puzzle puzzle, String userSubmissionJson, long timeSpentMs);
}
